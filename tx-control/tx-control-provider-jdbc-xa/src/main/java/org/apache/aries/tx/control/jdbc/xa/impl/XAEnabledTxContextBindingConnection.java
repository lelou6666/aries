package org.apache.aries.tx.control.jdbc.xa.impl;

import static org.osgi.service.transaction.control.TransactionStatus.NO_TRANSACTION;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import javax.sql.DataSource;
import javax.transaction.xa.XAResource;

import org.apache.aries.tx.control.jdbc.common.impl.ConnectionWrapper;
import org.apache.aries.tx.control.jdbc.common.impl.ScopedConnectionWrapper;
import org.apache.aries.tx.control.jdbc.common.impl.TxConnectionWrapper;
import org.osgi.service.transaction.control.LocalResource;
import org.osgi.service.transaction.control.TransactionContext;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.TransactionException;

public class XAEnabledTxContextBindingConnection extends ConnectionWrapper {

	private final TransactionControl	txControl;
	private final UUID					resourceId;
	private final DataSource			dataSource;
	private final boolean				xaEnabled;
	private final boolean				localEnabled;

	public XAEnabledTxContextBindingConnection(TransactionControl txControl,
			DataSource dataSource, UUID resourceId, boolean xaEnabled, boolean localEnabled) {
		this.txControl = txControl;
		this.dataSource = dataSource;
		this.resourceId = resourceId;
		this.xaEnabled = xaEnabled;
		this.localEnabled = localEnabled;
	}

	@Override
	protected final Connection getDelegate() {

		TransactionContext txContext = txControl.getCurrentContext();

		if (txContext == null) {
			throw new TransactionException("The resource " + dataSource
					+ " cannot be accessed outside of an active Transaction Context");
		}

		Connection existing = (Connection) txContext.getScopedValue(resourceId);

		if (existing != null) {
			return existing;
		}

		Connection toReturn;
		Connection toClose;

		try {
			if (txContext.getTransactionStatus() == NO_TRANSACTION) {
				toClose = dataSource.getConnection();
				toReturn = new ScopedConnectionWrapper(toClose);
			} else if (txContext.supportsXA() && xaEnabled) {
				toClose = dataSource.getConnection();
				toReturn = new TxConnectionWrapper(toClose);
				txContext.registerXAResource(getXAResource(toClose));
			} else if (txContext.supportsLocal() && localEnabled) {
				toClose = dataSource.getConnection();
				toReturn = new TxConnectionWrapper(toClose);
				txContext.registerLocalResource(getLocalResource(toClose));
			} else {
				throw new TransactionException(
						"There is a transaction active, but it does not support local participants");
			}
		} catch (Exception sqle) {
			throw new TransactionException(
					"There was a problem getting hold of a database connection",
					sqle);
		}

		
		txContext.postCompletion(x -> {
				try {
					toClose.close();
				} catch (SQLException sqle) {
					// TODO log this
				}
			});
		
		txContext.putScopedValue(resourceId, toReturn);
		
		return toReturn;
	}

	
	private XAResource getXAResource(Connection conn) throws SQLException {
		if(conn instanceof XAConnectionWrapper) {
			return ((XAConnectionWrapper)conn).getXaResource();
		} else if(conn.isWrapperFor(XAConnectionWrapper.class)){
			return conn.unwrap(XAConnectionWrapper.class).getXaResource();
		} else {
			throw new IllegalArgumentException("The XAResource for the connection cannot be found");
		}
	}
	
	private LocalResource getLocalResource(Connection conn) {
		return new LocalResource() {
			@Override
			public void commit() throws TransactionException {
				try {
					conn.commit();
				} catch (SQLException e) {
					throw new TransactionException(
							"An error occurred when committing the connection", e);
				}
			}

			@Override
			public void rollback() throws TransactionException {
				try {
					conn.rollback();
				} catch (SQLException e) {
					throw new TransactionException(
							"An error occurred when rolling back the connection", e);
				}
			}

		};
	}
}
