package org.apache.aries.tx.control.jdbc.local.impl;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.osgi.framework.Constants.OBJECTCLASS;
import static org.osgi.service.jdbc.DataSourceFactory.JDBC_DATABASE_NAME;
import static org.osgi.service.jdbc.DataSourceFactory.JDBC_DATASOURCE_NAME;
import static org.osgi.service.jdbc.DataSourceFactory.JDBC_DESCRIPTION;
import static org.osgi.service.jdbc.DataSourceFactory.JDBC_NETWORK_PROTOCOL;
import static org.osgi.service.jdbc.DataSourceFactory.JDBC_PASSWORD;
import static org.osgi.service.jdbc.DataSourceFactory.JDBC_PORT_NUMBER;
import static org.osgi.service.jdbc.DataSourceFactory.JDBC_ROLE_NAME;
import static org.osgi.service.jdbc.DataSourceFactory.JDBC_SERVER_NAME;
import static org.osgi.service.jdbc.DataSourceFactory.JDBC_URL;
import static org.osgi.service.jdbc.DataSourceFactory.JDBC_USER;
import static org.osgi.service.jdbc.DataSourceFactory.OSGI_JDBC_DRIVER_CLASS;

import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.jdbc.DataSourceFactory;
import org.osgi.service.transaction.control.jdbc.JDBCConnectionProvider;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ManagedServiceFactoryImpl implements ManagedServiceFactory {

	private static final String DSF_TARGET_FILTER = "aries.dsf.target.filter";
	private static final String JDBC_PROP_NAMES = "aries.jdbc.property.names";
	private static final List<String> JDBC_PROPERTIES = asList(JDBC_DATABASE_NAME, JDBC_DATASOURCE_NAME,
			JDBC_DESCRIPTION, JDBC_NETWORK_PROTOCOL, JDBC_PASSWORD, JDBC_PORT_NUMBER, JDBC_ROLE_NAME, JDBC_SERVER_NAME,
			JDBC_URL, JDBC_USER);

	private final Map<String, ManagedJDBCResourceProvider> managedInstances = new ConcurrentHashMap<>();

	private final BundleContext context;

	public ManagedServiceFactoryImpl(BundleContext context) {
		this.context = context;
	}

	@Override
	public String getName() {
		return "Aries JDBCConnectionProvider (Local only) service";
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> properties) throws ConfigurationException {

		Map<String, Object> propsMap = new HashMap<>();

		Enumeration<String> keys = properties.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			propsMap.put(key, properties.get(key));
		}

		Properties jdbcProps = getJdbcProps(propsMap);

		try {
			ManagedJDBCResourceProvider mjrp = new ManagedJDBCResourceProvider(context, jdbcProps, propsMap);
			ofNullable(managedInstances.put(pid, mjrp)).ifPresent(ManagedJDBCResourceProvider::stop);
			mjrp.start();
		} catch (InvalidSyntaxException e) {
			throw new ConfigurationException(DSF_TARGET_FILTER, "The target filter was invalid", e);
		}
	}

	public void stop() {
		managedInstances.values().forEach(ManagedJDBCResourceProvider::stop);
	}

	@SuppressWarnings("unchecked")
	private Properties getJdbcProps(Map<String, Object> properties) throws ConfigurationException {

		Object object = properties.getOrDefault(JDBC_PROP_NAMES, JDBC_PROPERTIES);
		Collection<String> propnames;
		if (object instanceof String) {
			propnames = Arrays.asList(((String) object).split(","));
		} else if (object instanceof String[]) {
			propnames = Arrays.asList((String[]) object);
		} else if (object instanceof Collection) {
			propnames = (Collection<String>) object;
		} else {
			throw new ConfigurationException(JDBC_PROP_NAMES,
					"The jdbc property names must be a String+ or comma-separated String");
		}

		Properties p = new Properties();

		propnames.stream().filter(properties::containsKey)
				.forEach(s -> p.setProperty(s, String.valueOf(properties.get(s))));

		return p;
	}

	@Override
	public void deleted(String pid) {
		ofNullable(managedInstances.remove(pid))
			.ifPresent(ManagedJDBCResourceProvider::stop);
	}

	private static class ManagedJDBCResourceProvider
			implements ServiceTrackerCustomizer<DataSourceFactory, DataSourceFactory> {

		private final BundleContext context;
		private final Properties jdbcProperties;
		private final Map<String, Object> providerProperties;
		private final ServiceTracker<DataSourceFactory, DataSourceFactory> dsfTracker;

		private final AtomicReference<DataSourceFactory> activeDsf = new AtomicReference<>();
		private final AtomicReference<ServiceRegistration<JDBCConnectionProvider>> serviceReg = new AtomicReference<>();

		public ManagedJDBCResourceProvider(BundleContext context, Properties jdbcProperties,
				Map<String, Object> providerProperties) throws InvalidSyntaxException, ConfigurationException {
			this.context = context;
			this.jdbcProperties = jdbcProperties;
			this.providerProperties = providerProperties;

			String targetFilter = (String) providerProperties.get(DSF_TARGET_FILTER);
			if (targetFilter == null) {
				String driver = (String) providerProperties.get(OSGI_JDBC_DRIVER_CLASS);
				if (driver == null) {
					throw new ConfigurationException(OSGI_JDBC_DRIVER_CLASS,
							"The configuration must specify either a target filter or a JDBC driver class");
				}
				targetFilter = "(" + OSGI_JDBC_DRIVER_CLASS + "=" + driver + ")";
			}

			targetFilter = "(&(" + OBJECTCLASS + "=" + DataSourceFactory.class.getName() + ")" + targetFilter + ")";

			this.dsfTracker = new ServiceTracker<>(context, context.createFilter(targetFilter), this);
		}

		public void start() {
			dsfTracker.open();
		}

		public void stop() {
			dsfTracker.close();
		}

		@Override
		public DataSourceFactory addingService(ServiceReference<DataSourceFactory> reference) {
			DataSourceFactory service = context.getService(reference);

			updateService(service);
			return service;
		}

		private void updateService(DataSourceFactory service) {
			boolean setDsf;
			synchronized (this) {
				setDsf = activeDsf.compareAndSet(null, service);
			}

			if (setDsf) {
				try {
					JDBCConnectionProvider provider = new JDBCConnectionProviderFactoryImpl().getProviderFor(service,
							jdbcProperties, providerProperties);
					ServiceRegistration<JDBCConnectionProvider> reg = context
							.registerService(JDBCConnectionProvider.class, provider, getServiceProperties());
					if (!serviceReg.compareAndSet(null, reg)) {
						throw new IllegalStateException("Unable to set the JDBC connection provider registration");
					}
				} catch (Exception e) {
					activeDsf.compareAndSet(service, null);
				}
			}
		}

		private Dictionary<String, ?> getServiceProperties() {
			Hashtable<String, Object> props = new Hashtable<>();
			providerProperties.keySet().stream().filter(s -> !JDBC_PASSWORD.equals(s))
					.forEach(s -> props.put(s, providerProperties.get(s)));
			return props;
		}

		@Override
		public void modifiedService(ServiceReference<DataSourceFactory> reference, DataSourceFactory service) {
		}

		@Override
		public void removedService(ServiceReference<DataSourceFactory> reference, DataSourceFactory service) {
			boolean dsfLeft;
			ServiceRegistration<JDBCConnectionProvider> oldReg = null;
			synchronized (this) {
				dsfLeft = activeDsf.compareAndSet(service, null);
				if (dsfLeft) {
					oldReg = serviceReg.getAndSet(null);
				}
			}

			if (oldReg != null) {
				try {
					oldReg.unregister();
				} catch (IllegalStateException ise) {

				}
			}

			if (dsfLeft) {
				DataSourceFactory newDSF = dsfTracker.getService();
				if (newDSF != null) {
					updateService(dsfTracker.getService());
				}
			}
		}
	}
}
