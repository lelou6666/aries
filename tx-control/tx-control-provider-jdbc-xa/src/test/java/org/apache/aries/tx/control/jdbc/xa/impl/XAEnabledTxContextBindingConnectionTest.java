package org.apache.aries.tx.control.jdbc.xa.impl;

import static org.mockito.Mockito.times;
import static org.osgi.service.transaction.control.TransactionStatus.ACTIVE;
import static org.osgi.service.transaction.control.TransactionStatus.NO_TRANSACTION;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAResource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.service.transaction.control.LocalResource;
import org.osgi.service.transaction.control.TransactionContext;
import org.osgi.service.transaction.control.TransactionControl;
import org.osgi.service.transaction.control.TransactionException;

@RunWith(MockitoJUnitRunner.class)
public class XAEnabledTxContextBindingConnectionTest {

	@Mock
	TransactionControl control;
	
	@Mock
	TransactionContext context;
	
	@Mock
	DataSource dataSource;
	
	@Mock
	XADataSource xaDataSource;

	@Mock
	XAConnection xaMock;
	
	@Mock
	XAResource xaResource;
	
	@Mock
	Connection rawConnection;
	
	Map<Object, Object> variables = new HashMap<>();
	
	UUID id = UUID.randomUUID();
	
	XAEnabledTxContextBindingConnection localConn;
	XAEnabledTxContextBindingConnection xaConn;
	
	@Before
	public void setUp() throws SQLException {
		Mockito.when(dataSource.getConnection()).thenReturn(rawConnection).thenReturn(null);
		
		Mockito.doAnswer(i -> variables.put(i.getArguments()[0], i.getArguments()[1]))
			.when(context).putScopedValue(Mockito.any(), Mockito.any());
		Mockito.when(context.getScopedValue(Mockito.any()))
			.thenAnswer(i -> variables.get(i.getArguments()[0]));
		
		Mockito.when(xaDataSource.getXAConnection()).thenReturn(xaMock);
		Mockito.when(xaMock.getConnection()).thenReturn(rawConnection);
		Mockito.when(xaMock.getXAResource()).thenReturn(xaResource);
		
		localConn = new XAEnabledTxContextBindingConnection(control, dataSource, id, false, true);
		xaConn = new XAEnabledTxContextBindingConnection(control, new XADataSourceMapper(xaDataSource), 
				id, true, false);
	}
	
	private void setupNoTransaction() {
		Mockito.when(control.getCurrentContext()).thenReturn(context);
		Mockito.when(context.getTransactionStatus()).thenReturn(NO_TRANSACTION);
	}

	private void setupLocalTransaction() {
		Mockito.when(control.getCurrentContext()).thenReturn(context);
		Mockito.when(context.supportsLocal()).thenReturn(true);
		Mockito.when(context.getTransactionStatus()).thenReturn(ACTIVE);
	}

	private void setupXATransaction() {
		Mockito.when(control.getCurrentContext()).thenReturn(context);
		Mockito.when(context.supportsXA()).thenReturn(true);
		Mockito.when(context.getTransactionStatus()).thenReturn(ACTIVE);
	}
	
	
	@Test(expected=TransactionException.class)
	public void testUnscopedLocal() throws SQLException {
		localConn.isValid(500);
	}

	@Test(expected=TransactionException.class)
	public void testUnscopedXA() throws SQLException {
		xaConn.isValid(500);
	}

	@Test
	public void testNoTransaction() throws SQLException {
		setupNoTransaction();
		
		localConn.isValid(500);
		localConn.isValid(500);
		
		Mockito.verify(rawConnection, times(2)).isValid(500);
		Mockito.verify(context, times(0)).registerLocalResource(Mockito.any());
		
		Mockito.verify(context).postCompletion(Mockito.any());
	}

	@Test
	public void testNoTransactionXA() throws SQLException {
		setupNoTransaction();
		
		xaConn.isValid(500);
		xaConn.isValid(500);
		
		Mockito.verify(rawConnection, times(2)).isValid(500);
		Mockito.verify(context, times(0)).registerLocalResource(Mockito.any());
		
		Mockito.verify(context).postCompletion(Mockito.any());
	}

	@Test
	public void testLocalTransactionCommit() throws SQLException {
		setupLocalTransaction();
		
		localConn.isValid(500);
		localConn.isValid(500);
		
		ArgumentCaptor<LocalResource> captor = ArgumentCaptor.forClass(LocalResource.class);

		Mockito.verify(rawConnection, times(2)).isValid(500);
		Mockito.verify(context).registerLocalResource(captor.capture());
		
		Mockito.verify(context).postCompletion(Mockito.any());
		
		captor.getValue().commit();
		
		Mockito.verify(rawConnection).commit();
	}

	@Test
	public void testLocalTransactionRollback() throws SQLException {
		setupLocalTransaction();
		
		localConn.isValid(500);
		localConn.isValid(500);
		
		ArgumentCaptor<LocalResource> captor = ArgumentCaptor.forClass(LocalResource.class);
		
		Mockito.verify(rawConnection, times(2)).isValid(500);
		Mockito.verify(context).registerLocalResource(captor.capture());
		
		Mockito.verify(context).postCompletion(Mockito.any());
		
		captor.getValue().rollback();
		
		Mockito.verify(rawConnection).rollback();
	}

	@Test(expected=TransactionException.class)
	public void testLocalTransactionNoLocal() throws SQLException {
		setupLocalTransaction();
		
		Mockito.when(context.supportsLocal()).thenReturn(false);
		localConn.isValid(500);
	}
	
	@Test(expected=TransactionException.class)
	public void testLocalConnWithXATransaction() throws SQLException {
		setupXATransaction();
		
		localConn.isValid(500);
	}

	@Test
	public void testXATransactionCommit() throws SQLException {
		setupXATransaction();
		
		xaConn.isValid(500);
		xaConn.isValid(500);
		
		
		Mockito.verify(rawConnection, times(2)).isValid(500);
		Mockito.verify(context).registerXAResource(xaResource);
		
		Mockito.verify(context).postCompletion(Mockito.any());
		
		Mockito.verify(rawConnection, times(0)).commit();
	}
	
	@Test
	public void testXATransactionRollback() throws SQLException {
		setupXATransaction();
		
		xaConn.isValid(500);
		xaConn.isValid(500);
		
		Mockito.verify(rawConnection, times(2)).isValid(500);
		Mockito.verify(context).registerXAResource(xaResource);
		
		Mockito.verify(context).postCompletion(Mockito.any());
		
		Mockito.verify(rawConnection, times(0)).rollback();
	}
	
	@Test(expected=TransactionException.class)
	public void testXAConnTransactionWithLocal() throws SQLException {
		setupLocalTransaction();
		
		xaConn.isValid(500);
	}

}
