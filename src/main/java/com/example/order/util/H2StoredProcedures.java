package com.example.order.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.StringJoiner;

/**
 * Class that defines the stored procedures for the database
 */
public class H2StoredProcedures {
    /**
     * Represents a stored-procedure that gets the total of all paid orders of a customer
     * @param conn Connection to the database
     * @param customerId Id of the customer
     * @return ResultSet that contains the result
     * @throws Exception In case of a database error
     */
    public static ResultSet getPaidOrderTotalFromCustomer(Connection conn, Long customerId) throws Exception {
        StringJoiner joiner = new StringJoiner(",");
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT order_id FROM orders ");
        sql.append("WHERE order_status='paid' AND order_customer_id=" + customerId);

        PreparedStatement ps = conn.prepareStatement(sql.toString());
        ResultSet results = ps.executeQuery();
        while (results.next())
            joiner.add(results.getString("order_id"));

        sql = new StringBuffer();
        sql.append("SELECT SUM( MULT(product_price, order_detail_quantity) ) FROM order_details, products ");
        sql.append("WHERE order_detail_product_id = product_id ");
        sql.append("AND order_detail_order_id IN (" + joiner.toString() + ")");

        ps = conn.prepareStatement(sql.toString());
        return ps.executeQuery();
    }

    /**
     * Represents a stored-procedure that multiplies two double values
     * @param d1 First value
     * @param d2 Second value
     * @return Multiplication result
     */
    public static double mult(double d1, double d2) {
        return d1 * d2;
    }
}
