package com.example.order;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;

import com.example.order.dao.TotalOrderDao;
import com.example.order.dto.ParamsDto;
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
@PrepareForTest(TotalOrderDao.class)
public class TotalOrderDaoTest {
    private Database databaseInstance;
    private TotalOrderDao daoInstance;
    private ParamsDto paramsDto;

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

        daoInstance = new TotalOrderDao(databaseInstance);

        paramsDto = new ParamsDto();
        paramsDto.setCustomerId(1);
    }

    @Test
    public void shouldGetConnectionObject() throws SQLException {
        Database databaseMock = Mockito.mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(databaseInstance.getConnection());

        TotalOrderDao dao = new TotalOrderDao(databaseMock);
        dao.getTotalAllPaidOrders(paramsDto);

        try {
            verify(databaseMock, atLeastOnce()).getConnection();
        } catch (Error ex) {
            fail("You didn't call the `getConnection()` method on the `Database` object.");
        }
    }

    @Test
    public void shouldCreatedCallableStatementObject() throws Exception {
        Method createCallableStatementMethod = daoInstance.getClass().getDeclaredMethod("createCallableStatement", Connection.class, long.class);
        createCallableStatementMethod.setAccessible(true);

        PreparedStatement result = (PreparedStatement) createCallableStatementMethod.invoke(
                daoInstance,
                databaseInstance.getConnection(), paramsDto.getCustomerId()
        );

        assertNotNull("The method `createCallableStatement()` should not return null.",
                result
        );

        // Get the private field with the parameters set
        JdbcParameterMetaData parameterMetaData = (JdbcParameterMetaData) result.getParameterMetaData();
        Field parametersField = parameterMetaData.getClass().getDeclaredField("parameters");
        parametersField.setAccessible(true);

        ArrayList<? extends ParameterInterface> parameters =
                (ArrayList<? extends ParameterInterface>) parametersField.get(parameterMetaData);

        assertNotNull("The method `createCallableStatement()` doesn't create a valid CallableStatement.",
                parameters
        );
        assertEquals("You should set the ID of the customer as a parameter on the `CallableStatement` object returned by the method `createCallableStatement()`.",
                1, parameters.size()
        );
        assertTrue("You should set the ID of the customer as a parameter (of type Long) on the `CallableStatement` object returned by the method `createCallableStatement()`.",
                parameters.get(0).isValueSet()
        );
    }

    @Test
    public void shouldCallStoredProcedure() throws Exception {
        TotalOrderDao daoMock = PowerMockito.spy(daoInstance);
        CallableStatement csMock = Mockito.mock(CallableStatement.class);

        PowerMockito.doReturn(csMock)
                .when(daoMock, "createCallableStatement", any(Connection.class), any(long.class));

        try {
            daoMock.getTotalAllPaidOrders(paramsDto);
        } catch (Exception ex) {
            // ex.printStackTrace();
        }

        try {
            verify(csMock, atLeastOnce()).execute();
        } catch (Error ex) {
            fail("You didn't call the `execute()` method on the `CallableStatement` object.");
        }
    }

    @Test
    public void shouldGetResultSetObject() throws Exception {
        TotalOrderDao daoMock = PowerMockito.spy(daoInstance);
        CallableStatement csMock = Mockito.mock(CallableStatement.class);

        PowerMockito.doReturn(csMock)
                .when(daoMock, "createCallableStatement", any(Connection.class), any(long.class));

        try {
            daoMock.getTotalAllPaidOrders(paramsDto);
        } catch (Exception ex) {
            // ex.printStackTrace();
        }

        try {
            verify(csMock, atLeastOnce()).getResultSet();
        } catch (Error ex) {
            fail("You didn't call the `getResultSet()` method on the `CallableStatement` object.");
        }
    }

    @Test
    public void shouldGetResultOfStoredProcedureFromResultSetObject() throws Exception {
        TotalOrderDao daoMock = PowerMockito.spy(daoInstance);
        CallableStatement csMock = Mockito.mock(CallableStatement.class);
        ResultSet rsMock = Mockito.mock(ResultSet.class);

        PowerMockito.doReturn(csMock)
                .when(daoMock, "createCallableStatement", any(Connection.class), any(long.class));
        when(csMock.getResultSet()).thenReturn(rsMock);
        when(rsMock.next()).thenReturn(true);

        daoMock.getTotalAllPaidOrders(paramsDto);

        try {
            verify(rsMock, times(1)).next();
        } catch (Error ex) {
            fail("You didn't call the `next()` method on the `ResultSet` object returned by the `CallableStatement` object.");
        }

        try {
            verify(rsMock, atLeastOnce()).getBigDecimal(1);
        } catch (Error ex) {
            fail("You didn't call the `getBigDecimal()` method on the `ResultSet` object returned by the `CallableStatement` object.");
        }

        try {
            BigDecimal result = daoInstance.getTotalAllPaidOrders(paramsDto);
            assertNotNull("The method `getTotalAllPaidOrders()` should not return a null value.",
                    result
            );
        } catch (Exception ex) {
            fail("The method `getTotalAllPaidOrders()` failed for the following reason: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
