package com.example.order.dao;

import com.example.order.dto.ParamsDto;
import com.example.order.util.Database;
import com.example.order.util.ExceptionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * DAO to delete an order
 */
public class DeleteOrderDao {
    private Database database;

    /**
     * Constructor
     *
     * @param database Database object
     */
    public DeleteOrderDao(Database database) {
        this.database = database;
    }

    /**
     * Deletes one or more orders using their IDs
     *
     * @param paramsDto Object with the parameters for the operation
     * @return Number of orders deleted
     */
    public int deleteOrdersById(ParamsDto paramsDto) {
        int numberResults = 0;

        try (Connection con = database.getConnection();
             PreparedStatement ps = createPreparedStatement(con, paramsDto.getOrderIds())
        ) {
            numberResults = ps.executeUpdate();

        } catch (SQLException ex) {
            ExceptionHandler.handleException(ex);
        }

        return numberResults;
    }

    /**
     * Method to build the delete SQL statement
     *
     * @param orderIds IDs of the orders to delete
     * @return Delete SQL statement
     */
    private String buildDeleteSql(List<Long> orderIds) {
        String ids = String.join(",", Collections.nCopies(orderIds.size(), "?"));

        return "DELETE FROM orders o WHERE o.order_id IN (" + ids + ")";
    }

    /**
     * Creates a PreparedStatement object to delete one or more orders
     *
     * @param con      Connnection object
     * @param orderIds Order IDs to set on the PreparedStatement
     * @return A PreparedStatement object
     * @throws SQLException In case of an error
     */
    private PreparedStatement createPreparedStatement(Connection con, List<Long> orderIds) throws SQLException {
        String sql = buildDeleteSql(orderIds);
        PreparedStatement ps = con.prepareStatement(sql);

        int i = 1;
        for (Long id : orderIds) {
            ps.setLong(i++, id);
        }

        return ps;
    }
}
