package com.example.order;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

import com.example.order.dao.InsertOrderDao;
import com.example.order.dto.OrderDetailDto;
import com.example.order.dto.OrderDto;
import com.example.order.util.Database;
import org.h2.expression.ParameterInterface;
import org.h2.jdbc.JdbcParameterMetaData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(InsertOrderDao.class)
public class InsertOrderDaoTest {
    private Database databaseInstance;
    private InsertOrderDao daoInstance;
    private OrderDto orderDto;

    static {
        try {
            // In case PowerMock doesn't load the driver
            java.sql.DriverManager.registerDriver(new org.h2.Driver());
        } catch (SQLException e) {
            // e.printStackTrace();
        }
    }

    @Before
    public void setup() {
        databaseInstance = Database.getInstance();

        daoInstance = new InsertOrderDao(databaseInstance);

        orderDto = new OrderDto();
        orderDto.setCustomerId(1);
        OrderDetailDto orderDetailDto = new OrderDetailDto();
        orderDetailDto.setProductId(1);
        orderDetailDto.setQuantity(1);
        orderDto.setOrderDetail(Collections.singletonList(orderDetailDto));
    }

    @Test
    public void shouldGetConnectionObject() throws SQLException, IOException {
        Database databaseMock = Mockito.mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(databaseInstance.getConnection());

        InsertOrderDao dao = new InsertOrderDao(databaseMock);
        try {
            dao.insertOrder(orderDto);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        try {
            verify(databaseMock, atLeastOnce()).getConnection();
        } catch (Error ex) {
            fail("You didn't call the `getConnection()` method on the `Database` object.");
        }
    }

    @Test
    public void shouldCreatePreparedStatementObjectForMainOrderRecord() throws Exception {
        Method createPreparedStatementMethod = daoInstance.getClass().getDeclaredMethod("createOrderPreparedStatement", Connection.class, OrderDto.class);
        createPreparedStatementMethod.setAccessible(true);

        PreparedStatement result = (PreparedStatement) createPreparedStatementMethod.invoke(
                daoInstance,
                databaseInstance.getConnection(), orderDto
        );

        assertNotNull("The method `createOrderPreparedStatement()` should not return null.",
                result
        );

        // Get the private field to see if generate keys is set
        Field generatedKeysField = result.getClass().getDeclaredField("generatedKeysRequest");
        generatedKeysField.setAccessible(true);

        Boolean flag = (Boolean) generatedKeysField.get(result);
        assertTrue("The method `createOrderPreparedStatement()` doesn't return a `PreparedStatement` object with the flag indicating whether auto-generated keys should be returned set.",
                flag != null && flag
        );

        // Get the private field with the parameters set
        JdbcParameterMetaData parameterMetaData = (JdbcParameterMetaData) result.getParameterMetaData();
        Field parametersField = parameterMetaData.getClass().getDeclaredField("parameters");
        parametersField.setAccessible(true);

        ArrayList<? extends ParameterInterface> parameters =
                (ArrayList<? extends ParameterInterface>) parametersField.get(parameterMetaData);

        assertNotNull("The method `createOrderPreparedStatement()` doesn't create a valid PreparedStatement with the insert query.",
                parameters
        );
        assertEquals("You should set the ID of the customer, the date of the order, and the order status as parameters on the PreparedStatement object returned by the method `createOrderPreparedStatement()`.",
                3, parameters.size()
        );
        assertTrue("You should set the ID of the customer as the first parameter (of type Long) on the PreparedStatement object returned by the method `createOrderPreparedStatement()`.",
                parameters.get(0).isValueSet()
        );
        assertTrue("You should set the date of the order as the second parameter (of type Timestamp) on the PreparedStatement object returned by the method `createOrderPreparedStatement()`.",
                parameters.get(1).isValueSet()
        );
        assertTrue("You should set the status of the order as the third parameter (of type String) on the PreparedStatement object returned by the method `createOrderPreparedStatement()`.",
                parameters.get(2).isValueSet()
        );
    }

    @Test
    public void shouldDisableAutoCommitMode() throws Exception {
        // Set autocommit to false
        Database databaseMock = Mockito.mock(Database.class);
        Connection connectionMock = Mockito.mock(Connection.class);
        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString(), anyInt())).thenReturn(Mockito.mock(PreparedStatement.class));

        InsertOrderDao dao = new InsertOrderDao(databaseMock);
        try {
            dao.insertOrder(orderDto);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        try {
            verify(connectionMock, times(1)).setAutoCommit(false);
        } catch (Error ex) {
            fail("You didn't call the `setAutoCommit(false)` method on the `Connection` object.");
        }
    }

    @Test
    public void shouldExecuteInsertOperation() throws Exception {
        // Execute update
        InsertOrderDao daoMock = PowerMockito.spy(daoInstance);
        PreparedStatement psMock = Mockito.mock(PreparedStatement.class);

        PowerMockito.doReturn(psMock)
                .when(daoMock, "createOrderPreparedStatement", any(Connection.class), any(OrderDto.class));

        daoMock.insertOrder(orderDto);

        try {
            verify(psMock, times(1)).executeUpdate();
        } catch (Error ex) {
            fail("You didn't call the `executeUpdate()` method on the `PreparedStatement` object returned by the method `createOrderPreparedStatement()`.");
        }
    }

    @Test
    public void shouldGetResultSetObjectToGetIdentifierOfInsertedOrder() throws Exception {
        // get Generated Keys
        InsertOrderDao daoMock = PowerMockito.spy(daoInstance);
        PreparedStatement psMock = Mockito.mock(PreparedStatement.class);

        PowerMockito.doReturn(psMock)
                .when(daoMock, "createOrderPreparedStatement", any(Connection.class), any(OrderDto.class));

        daoMock.insertOrder(orderDto);

        try {
            verify(psMock, atLeastOnce()).getGeneratedKeys();
        } catch (Error ex) {
            fail("You didn't call the `getGeneratedKeys()` method on the `PreparedStatement` object.");
        }
    }

    @Test
    public void shouldRollbackTransactionOnError() throws Exception {
        // Rollback connection
        Database databaseMock = Mockito.mock(Database.class);
        Connection connectionMock = Mockito.mock(Connection.class);
        PreparedStatement psMock = Mockito.mock(PreparedStatement.class);
        ResultSet rsMock = Mockito.mock(ResultSet.class);

        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString(), anyInt())).thenReturn(psMock);
        when(psMock.getGeneratedKeys()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(false);

        InsertOrderDao dao = new InsertOrderDao(databaseMock);
        try {
            dao.insertOrder(orderDto);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        try {
            verify(connectionMock, times(1)).rollback();
        } catch (Error ex) {
            fail("You didn't call the `rollback()` method on the `Connection` object.");
        }
    }

    @Test
    public void shouldGetIdentifierOfInsertedOrder() throws Exception {
        // Get order id of the new record
        InsertOrderDao daoMock = PowerMockito.spy(daoInstance);
        PreparedStatement psMock = Mockito.mock(PreparedStatement.class);
        ResultSet rsMock = Mockito.mock(ResultSet.class);

        PowerMockito.doReturn(psMock)
                .when(daoMock, "createOrderPreparedStatement", any(Connection.class), any(OrderDto.class));
        when(psMock.getGeneratedKeys()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(true);
        when(rsMock.getLong(1)).thenReturn(1L);

        long orderId = daoMock.insertOrder(orderDto);

        boolean columnIndex = true;
        boolean columnName = true;

        try {
            verify(rsMock, times(1)).getLong(1);
        } catch (Error ex) {
            columnIndex = false;
        }

        try {
            verify(rsMock, times(1)).getLong("order_id");
        } catch (Error ex) {
            columnName = false;
        }

        if (!columnIndex && !columnName) {
            fail("You didn't get the ID of the new order using the `getLong()` method of the `ResultSet` object.");
        }

        assertNotEquals("You didn't assign the ID of the new order to the variable `orderId`",
                -1, orderId
        );
    }

    @Test
    public void shouldCreatedPreparedStatementObjectForOrderDetailOfMainOrder() throws Exception {
        // createOrderDetailPreparedStatement
        Method createPreparedStatementMethod = daoInstance.getClass().getDeclaredMethod("createOrderDetailPreparedStatement", Connection.class, OrderDetailDto.class);
        createPreparedStatementMethod.setAccessible(true);

        PreparedStatement result = (PreparedStatement) createPreparedStatementMethod.invoke(
                daoInstance,
                databaseInstance.getConnection(), orderDto.getOrderDetail().get(0)
        );

        assertNotNull("The method `createOrderDetailPreparedStatement()` should not return null.",
                result
        );

        // Get the private field with the parameters set
        JdbcParameterMetaData parameterMetaData = (JdbcParameterMetaData) result.getParameterMetaData();
        Field parametersField = parameterMetaData.getClass().getDeclaredField("parameters");
        parametersField.setAccessible(true);

        ArrayList<? extends ParameterInterface> parameters =
                (ArrayList<? extends ParameterInterface>) parametersField.get(parameterMetaData);

        assertNotNull("The method `createOrderDetailPreparedStatement()` doesn't create a valid PreparedStatement with the insert query.",
                parameters
        );
        assertEquals("You should set the ID of the order, the product ID, and the quantity of the product as parameters on the PreparedStatement object returned by the method `createOrderDetailPreparedStatement()`.",
                3, parameters.size()
        );
        assertTrue("You should set the ID of the order as the first parameter (of type Long) on the PreparedStatement object returned by the method `createOrderDetailPreparedStatement()`.",
                parameters.get(0).isValueSet()
        );
        assertTrue("You should set the product ID as the second parameter (of type Long) on the PreparedStatement object returned by the method `createOrderDetailPreparedStatement()`.",
                parameters.get(1).isValueSet()
        );
        assertTrue("You should set the quantity of the product as the third parameter (of type Int) on the PreparedStatement object returned by the method `createOrderDetailPreparedStatement()`.",
                parameters.get(2).isValueSet()
        );
    }

    @Test
    public void shouldExecuteInsertOperationOnOrderDetail() throws Exception {
        // Execute update on second PreparedStatement
        InsertOrderDao daoMock = PowerMockito.spy(daoInstance);
        PreparedStatement psMock = Mockito.mock(PreparedStatement.class);

        PowerMockito.doReturn(psMock)
                .when(daoMock, "createOrderDetailPreparedStatement", any(Connection.class), any(OrderDetailDto.class));

        try {
            daoMock.insertOrder(orderDto);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        try {
            verify(psMock, times(1)).executeUpdate();
        } catch (Error ex) {
            fail("You didn't call the `executeUpdate()` method on the `PreparedStatement` returned by the method `createOrderDetailPreparedStatement()`.");
        }
    }

    @Test
    public void shouldRollbackTransactionIfNothingWasInserted() throws Exception {
        // Rollback if nothing was inserted
        Database databaseMock = Mockito.mock(Database.class);
        Connection connectionMock = Mockito.mock(Connection.class);
        PreparedStatement psMock = Mockito.mock(PreparedStatement.class);
        PreparedStatement psDetailMock = Mockito.mock(PreparedStatement.class);
        ResultSet rsMock = Mockito.mock(ResultSet.class);

        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString(), anyInt())).thenReturn(psMock);
        when(psMock.getGeneratedKeys()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(true);
        when(connectionMock.prepareStatement(anyString())).thenReturn(psDetailMock);
        when(psDetailMock.executeUpdate()).thenReturn(0);

        InsertOrderDao dao = new InsertOrderDao(databaseMock);
        long orderId = -9999;
        try {
            orderId = dao.insertOrder(orderDto);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        try {
            verify(connectionMock, times(1)).rollback();
        } catch (Error ex) {
            fail("You didn't call the `rollback()` method on the `Connection` object.");
        }
        assertEquals("You didn't return an invalid order ID from the method `insertOrder`",
                -1, orderId
        );
    }

    @Test
    public void shouldCommitTransaction() throws Exception {
        // Commit transaction
        Database databaseMock = Mockito.mock(Database.class);
        Connection connectionMock = Mockito.mock(Connection.class);
        PreparedStatement psMock = Mockito.mock(PreparedStatement.class);
        PreparedStatement psDetailMock = Mockito.mock(PreparedStatement.class);
        ResultSet rsMock = Mockito.mock(ResultSet.class);

        when(databaseMock.getConnection()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(anyString(), anyInt())).thenReturn(psMock);
        when(psMock.getGeneratedKeys()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(true);
        when(connectionMock.prepareStatement(anyString())).thenReturn(psDetailMock);
        when(psDetailMock.executeUpdate()).thenReturn(1);

        InsertOrderDao dao = new InsertOrderDao(databaseMock);
        long orderId = -1;
        try {
            orderId = dao.insertOrder(orderDto);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        try {
            verify(connectionMock, times(1)).commit();
        } catch (Error ex) {
            fail("You didn't call the `commit()` method on the `Connection` object.");
        }
        assertNotEquals("You didn't return a valid order ID from the method `insertOrder`",
                -1, orderId
        );
    }

    @Test
    public void shouldRollbackTransactionOnException() throws Exception {
        // Rollback transaction on exception
        Database databaseMock = Mockito.mock(Database.class);
        InsertOrderDao daoMock = PowerMockito.spy(new InsertOrderDao(databaseMock));
        Connection connectionMock = Mockito.mock(Connection.class);
        PreparedStatement psMock = Mockito.mock(PreparedStatement.class);
        ResultSet rsMock = Mockito.mock(ResultSet.class);

        when(databaseMock.getConnection()).thenReturn(connectionMock);
        PowerMockito.doReturn(psMock)
                .when(daoMock, "createOrderPreparedStatement", any(Connection.class), any(OrderDto.class));
        when(psMock.getGeneratedKeys()).thenReturn(rsMock);
        when(rsMock.next()).thenThrow(SQLException.class);

        try {
            daoMock.insertOrder(orderDto);
        } catch (Exception ex) {
            //ex.printStackTrace();
        }

        try {
            verify(connectionMock, times(1)).rollback();
        } catch (Error ex) {
            fail("You didn't call the `rollback()` method on the `Connection` object.");
        }
    }
}
