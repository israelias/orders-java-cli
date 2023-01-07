package com.example.order.util;

import java.sql.SQLException;

/**
 * Utility class to handle exceptions
 */
public class ExceptionHandler {

    /**
     * Method to extract and print information from a SQLException
     *
     * @param sqlException Exception from which information will be extracted
     */
    public static void handleException(SQLException sqlException) {
        System.out.println(sqlException.getErrorCode());
        System.out.println(sqlException.getSQLState());
        System.out.println(sqlException.getMessage());
        sqlException.printStackTrace();

    }
}
