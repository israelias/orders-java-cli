package com.example.order;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.order.dao.DeleteOrderDao;
import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;
import org.h2.expression.ParameterInterface;
import org.h2.jdbc.JdbcParameterMetaData;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.WantedButNotInvoked;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DeleteOrderDao.class)
public class DeleteOrderDaoTest {
    private Database databaseInstance;
    private DeleteOrderDao daoInstance;
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

        daoInstance = new DeleteOrderDao(databaseInstance);

        paramsDto = new ParamsDto();
        paramsDto.setOrderIds(Arrays.asList(1L, 2L, 3L, 4L));
    }

    @Test
    public void shouldCreateDatabaseConnection() throws SQLException {
        Database databaseMock = Mockito.mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(databaseInstance.getConnection());

        DeleteOrderDao dao = new DeleteOrderDao(databaseMock);
        // Redefine the id to delete to avoid conflict with other tests
        paramsDto.setOrderIds(Arrays.asList(99L));
        dao.deleteOrdersById(paramsDto);

        try {
            verify(databaseMock, atLeastOnce()).getConnection();
        } catch (WantedButNotInvoked ex) {
            fail("You didn't call the `getConnection()` method on the `Database` object.");
        }
    }

    @Test
    public void shouldBuildDeleteSQLString() throws Exception {
        Method buildDeleteSqlMethod = daoInstance.getClass().getDeclaredMethod("buildDeleteSql", List.class);
        buildDeleteSqlMethod.setAccessible(true);

        List<Long> orderIds = paramsDto.getOrderIds();

        String sql = (String) buildDeleteSqlMethod.invoke(daoInstance, orderIds);
        int numberOfQuestionsMarks = countCharOccurrences(sql, '?');
        int numberOfCommas = countCharOccurrences(sql, ',');

        assertEquals("The number of question marks in the query doesn't match the number of parameters: " + sql,
                orderIds.size(), numberOfQuestionsMarks);

        assertEquals("The number of commas to separate the question marks in the query is not correct: " + sql,
                (orderIds.size() - 1), numberOfCommas);
    }

    @Test
    public void shouldCreatePreparedStatementObject() throws Exception {
        Method createPreparedStatementMethod = daoInstance.getClass().getDeclaredMethod("createPreparedStatement", Connection.class, List.class);
        createPreparedStatementMethod.setAccessible(true);

        List<Long> orderIds = paramsDto.getOrderIds();

        PreparedStatement result = (PreparedStatement) createPreparedStatementMethod.invoke(
                daoInstance,
                databaseInstance.getConnection(), orderIds
        );

        assertNotNull("The method `createPreparedStatement()` should not return null.",
                result
        );

        // Get the private field with the parameters set
        JdbcParameterMetaData parameterMetaData = (JdbcParameterMetaData) result.getParameterMetaData();
        Field parametersField = parameterMetaData.getClass().getDeclaredField("parameters");
        parametersField.setAccessible(true);

        ArrayList<? extends ParameterInterface> parameters =
                (ArrayList<? extends ParameterInterface>) parametersField.get(parameterMetaData);

        assertNotNull("The method `createPreparedStatement()` doesn't create a valid PreparedStatement with the delete query.",
                parameters
        );
        assertEquals("You should set all the IDs of the orders as parameters on the PreparedStatement object returned by the method `createPreparedStatement()`.",
                orderIds.size(), parameters.size()
        );
        for (int i = 0; i < orderIds.size(); i++) {
            if (!parameters.get(i).isValueSet()) {
                String whichParameterIsNotSet;
                if (i == 0) {
                    whichParameterIsNotSet = "You are not setting the first parameter of the query";
                } else if (i == orderIds.size() - 1) {
                    whichParameterIsNotSet = "You are not setting the last parameter of the query";
                } else {
                    whichParameterIsNotSet = "You are not setting all the parameters of the query";
                }
                fail(whichParameterIsNotSet + ". You should set all the IDs of the orders as parameters on the PreparedStatement object returned by the method `createPreparedStatement()`.");
            }
        }
    }

    @Test
    public void shouldExecuteDeleteOperation() throws Exception {
        DeleteOrderDao daoMock = PowerMockito.spy(daoInstance);
        PreparedStatement psMock = Mockito.mock(PreparedStatement.class);

        PowerMockito.doReturn(psMock)
                .when(daoMock, "createPreparedStatement", any(Connection.class), any(List.class));

        daoMock.deleteOrdersById(paramsDto);

        try {
            verify(psMock, atLeastOnce()).executeUpdate();
        } catch (WantedButNotInvoked ex) {
            fail("You didn't call the `executeUpdate()` method on the `PreparedStatement` object.");
        }

        int numberAffectedRows = daoInstance.deleteOrdersById(paramsDto);
        assertEquals("The `deleteOrdersById()` method is not returning the correct number of affected rows. Make sure you're retuning the result of the call to the `executeUpdate()` method.",
                paramsDto.getOrderIds().size(), numberAffectedRows);
    }

    private int countCharOccurrences(@NotNull String someString, char someChar) {
        int count = 0;

        for (int i = 0; i < someString.length(); i++) {
            if (someString.charAt(i) == someChar) {
                count++;
            }
        }

        return count;
    }
}
