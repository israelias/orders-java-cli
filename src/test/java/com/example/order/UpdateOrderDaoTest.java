package com.example.order;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import com.example.order.dao.UpdateOrderDao;
import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;
import org.h2.expression.ParameterInterface;
import org.h2.jdbc.JdbcParameterMetaData;
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
@PrepareForTest(UpdateOrderDao.class)
public class UpdateOrderDaoTest {
    private Database databaseInstance;
    private UpdateOrderDao daoInstance;
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

        daoInstance = new UpdateOrderDao(databaseInstance);

        paramsDto = new ParamsDto();
        paramsDto.setOrderId(1);
        paramsDto.setStatus("paid");
    }

    @Test
    public void shouldCreateDatabaseConnection() throws SQLException {
        Database databaseMock = Mockito.mock(Database.class);
        when(databaseMock.getConnection()).thenReturn(databaseInstance.getConnection());

        UpdateOrderDao dao = new UpdateOrderDao(databaseMock);
        dao.updateOrderStatus(new ParamsDto());

        try {
            verify(databaseMock, atLeastOnce()).getConnection();
        } catch (WantedButNotInvoked ex) {
            fail("You didn't call the `getConnection()` method on the `Database` object.");
        }
    }

    @Test
    public void shouldCreatePreparedStatementObject() throws Exception {
        Method createPreparedStatementMethod = daoInstance.getClass().getDeclaredMethod("createPreparedStatement", Connection.class, ParamsDto.class);
        createPreparedStatementMethod.setAccessible(true);

        PreparedStatement result = (PreparedStatement) createPreparedStatementMethod.invoke(
                daoInstance,
                databaseInstance.getConnection(), paramsDto
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

        assertNotNull("The method `createPreparedStatement()` doesn't create a valid PreparedStatement with the update query.",
                parameters
        );
        assertEquals("You should set the ID of the order and the new status as parameters on the PreparedStatement object returned by the method `createPreparedStatement()`.",
                2, parameters.size()
        );
        assertTrue("You should set the status of the order as the first parameter (of type String) on the PreparedStatement object returned by the method `createPreparedStatement()`.",
                parameters.get(0).isValueSet()
        );
        assertTrue("You should set the ID of the order as the second parameter (of type Long) on the PreparedStatement object returned by the method `createPreparedStatement()`.",
                parameters.get(1).isValueSet()
        );
    }

    @Test
    public void shouldExecuteUpdateOperation() throws Exception {
        UpdateOrderDao daoMock = PowerMockito.spy(daoInstance);
        PreparedStatement psMock = Mockito.mock(PreparedStatement.class);

        PowerMockito.doReturn(psMock)
                .when(daoMock, "createPreparedStatement", any(Connection.class), any(ParamsDto.class));

        daoMock.updateOrderStatus(paramsDto);

        try {
            verify(psMock, atLeastOnce()).executeUpdate();
        } catch (WantedButNotInvoked ex) {
            fail("You didn't call the `executeUpdate()` method on the `PreparedStatement` object.");
        }

        int numberAffectedRows = daoInstance.updateOrderStatus(paramsDto);
        assertEquals("The `updateOrderStatus()` method is not returning the correct number of affected rows. Make sure you're retuning the result of the call to the `executeUpdate()` method.",
                1, numberAffectedRows);
    }
}
