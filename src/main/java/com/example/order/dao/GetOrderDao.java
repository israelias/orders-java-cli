package com.example.order.dao;

import com.example.order.dto.OrderDto;
import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;
import com.example.order.util.ExceptionHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * DAO to get an order
 */
public class GetOrderDao {
    private final static String query = "SELECT * FROM orders o WHERE o.order_id = ?";
    private final Database database;

    /**
     * Constructor
     *
     * @param database Database object
     */
    public GetOrderDao(Database database) {
        this.database = database;
    }

    /**
     * Gets an order by its ID
     *
     * @param paramsDto Object with the parameters for the operation
     * @return Object with the main information of an order
     */
    public OrderDto getOrderById(ParamsDto paramsDto) {
        OrderDto orderDto = null;

        try (Connection con = database.getConnection();
             PreparedStatement ps = createPreparedStatement(con, paramsDto.getOrderId());
             ResultSet rs = createResultSet(ps)
        ) {
            if (rs.first()) {
                orderDto = new OrderDto();
                /* 'order_id' */
                orderDto.setOrderId(rs.getLong(1));
                /* 'order_customer_id' */
                orderDto.setCustomerId(rs.getLong(2));
                /* 'order_date' */
                orderDto.setDate(rs.getDate(3));
                /* 'order_status' */
                orderDto.setStatus(rs.getString(4));
            }

        } catch (SQLException | IOException ex) {
            ExceptionHandler.handleException((SQLException) ex);
        }

        return orderDto;
    }

    /**
     * Creates a PreparedStatement object to get an order
     *
     * @param con     Connection object
     * @param orderId Order ID to set on the PreparedStatement
     * @return A PreparedStatement object
     * @throws SQLException In case of an error
     */
    private @NotNull PreparedStatement createPreparedStatement(@NotNull Connection con, long orderId) throws SQLException {
        PreparedStatement ps = con.prepareStatement(query);
        ps.setLong(1, orderId);
        return ps;
    }

    /**
     * Creates a ResultSet object to get the results of the query
     *
     * @param ps PreparedStatement object to create the query
     * @return A ResultSet object
     * @throws SQLException In case of an error
     */
    private ResultSet createResultSet(@NotNull PreparedStatement ps) throws SQLException {
        return ps.executeQuery();
    }
}
