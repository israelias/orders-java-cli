package com.example.order.dao;

import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;
import com.example.order.util.ExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

/**
 * DAO to get the total of all the paid orders of a customer
 */
public class TotalOrderDao {
    private final static String query = "{call GET_PAID_ORDER_TOTAL_FROM_CUSTOMER(?)}";
    private final Database database;

    /**
     * Constructor
     *
     * @param database Database object
     */
    public TotalOrderDao(Database database) {
        this.database = database;
    }

    /**
     * Gets the total of all paid orders of a customer
     *
     * @param paramsDto Object with the arguments of the operation
     * @return Total of all paid orders
     */
    public BigDecimal getTotalAllPaidOrders(ParamsDto paramsDto) throws SQLException, IOException {
        BigDecimal result = null;

        try (Connection con = database.getConnection();
             CallableStatement cs = createCallableStatement(con, paramsDto.getCustomerId())
        ) {
            cs.execute();

            try (ResultSet resultSet = cs.getResultSet()) {
                if (resultSet != null && resultSet.next()) {
                    result = resultSet.getBigDecimal(1);
                }
            }

        } catch (SQLException ex) {
            ExceptionHandler.handleException(ex);
        }

        return result;
    }

    /**
     * Creates a CallableStatement object to get the total of the orders
     *
     * @param con        Connection object
     * @param customerId ID of the customer to set on the PreparedStatement
     * @return A PreparedStatement object
     * @throws SQLException In case of an error
     */
    private @NotNull CallableStatement createCallableStatement(@NotNull Connection con, long customerId) throws SQLException {
        CallableStatement cs = con.prepareCall(query);
        cs.setLong(1, customerId);

        return cs;
    }
}
