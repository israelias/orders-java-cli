package com.example.order;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.order.dao.GetOrderDao;
import com.example.order.dto.OrderDto;
import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;
import com.example.order.util.ExceptionHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class GetOrderDaoTest {
    private Database databaseInstance;
    private Database databaseMock;
    private GetOrderDao daoInstance;

    @Before
    public void setup() {
        databaseInstance = Database.getInstance();

        databaseMock = Mockito.mock(Database.class);

        daoInstance = new GetOrderDao(databaseInstance);
    }

    @Test
    public void shouldCreateDatabaseConnection() throws SQLException, IOException {
        when(databaseMock.getConnection()).thenReturn(databaseInstance.getConnection());

        GetOrderDao getOrderDao = new GetOrderDao(databaseMock);
        getOrderDao.getOrderById(new ParamsDto());

        try {
            verify(databaseMock, atLeastOnce()).getConnection();
        } catch (WantedButNotInvoked ex) {
            fail("Please call the `getConnection()` method on the `Database` object.");
        }
    }

    @Test
    public void shouldCreatePreparedStatementObject() throws Exception {
        Method createPreparedStatementMethod = daoInstance.getClass().getDeclaredMethod("createPreparedStatement", Connection.class, long.class);
        createPreparedStatementMethod.setAccessible(true);

        PreparedStatement result = (PreparedStatement) createPreparedStatementMethod.invoke(
                daoInstance,
                databaseInstance.getConnection(), 1L
        );

        assertNotNull("The method `createPreparedStatement()` should not return null.",
                result
        );
        assertEquals("You should set the ID of the order as a parameter on the PreparedStatement object returned by the method `createPreparedStatement()`.", 1, result.getParameterMetaData().getParameterCount());
        assertTrue("You should set the ID of the order as a parameter of type Long on the PreparedStatement object returned by the method `createPreparedStatement()`.",
                result.getParameterMetaData().getParameterClassName(1).contains("Long")
        );
    }

    @Test
    public void shouldCreateResultSetObject() throws Exception {
        Method createResultSetMethod = daoInstance.getClass().getDeclaredMethod("createResultSet", PreparedStatement.class);
        createResultSetMethod.setAccessible(true);

        PreparedStatement preparedStatementMock = Mockito.mock(PreparedStatement.class);
        when(preparedStatementMock.executeQuery()).thenReturn(Mockito.mock(ResultSet.class));

        ResultSet result = (ResultSet) createResultSetMethod.invoke(
                daoInstance,
                preparedStatementMock
        );

        assertNotNull("The method `createResultSet()` should not return null.",
                result
        );

        try {
            verify(preparedStatementMock, atLeastOnce()).executeQuery();
        } catch (WantedButNotInvoked ex) {
            fail("You didn't call the `executeQuery()` method on the `PreparedStatement` object.");
        }
    }

    @Test
    public void shouldExtractQueryResultsFromResultSetObject() {
        // Create an orderDTO
        ParamsDto paramsDto = new ParamsDto();
        long orderId = 1;
        paramsDto.setOrderId(orderId);

        OrderDto orderDto = daoInstance.getOrderById(paramsDto);
        assertNotNull("The method `getOrderById()` should not return null",
                orderDto
        );
        assertEquals(String.format(
                "The object returned by the method `getOrderById()` doesn't contain the correct order ID. Expected: %s. Returned: %s.",
                orderId, orderDto.getOrderId()
        ), orderDto.getOrderId(), orderId);
        assertTrue("The object returned by the method `getOrderById()` doesn't contain a customer ID. Verify you're setting this property correctly.",
                orderDto.getCustomerId() > 0);
        assertNotNull("The object returned by the method `getOrderById()` doesn't contain an order date. Verify you're setting this property correctly.", orderDto.getDate());
        assertNotNull("The object returned by the method `getOrderById()` doesn't contain an order status. Verify you're setting this property correctly.", orderDto.getStatus());
    }

    @Test
    public void shouldExtractInformationFromSQLException() {
        // Implement ExceptionHandler.handleException
        SQLException sqlExceptionMock = Mockito.mock(SQLException.class);

        ExceptionHandler.handleException(sqlExceptionMock);

        try {
            verify(sqlExceptionMock, atLeastOnce()).getErrorCode();
        } catch (WantedButNotInvoked ex) {
            fail("You didn't call the `getErrorCode()` method on the `SQLException` object.");
        }

        try {
            verify(sqlExceptionMock, atLeastOnce()).getSQLState();
        } catch (WantedButNotInvoked ex) {
            fail("You didn't call the `getSQLState()` method on the `SQLException` object.");
        }

        try {
            verify(sqlExceptionMock, atLeastOnce()).getMessage();
        } catch (WantedButNotInvoked ex) {
            fail("You didn't call the `getMessage()` method on the `SQLException` object.");
        }

        try {
            verify(sqlExceptionMock, atLeastOnce()).printStackTrace();
        } catch (WantedButNotInvoked ex) {
            fail("You didn't call the `printStackTrace()` method on the `SQLException` object.");
        }
    }
}
