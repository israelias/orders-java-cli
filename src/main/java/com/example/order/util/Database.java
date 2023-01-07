package com.example.order.util;

import org.h2.tools.RunScript;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to get database connections
 */
public class Database {
    private static Database instance = null;
    private static boolean isInitialized = false;

    private final String url = "jdbc:h2:mem:orders;DB_CLOSE_DELAY=-1";
    private final String user = "sa";
    private final String password = "";

    /**
     * Private constructor
     */
    private Database() {
    }

    /**
     * Method that creates an instance of this class (if it's not created already)
     *
     * @return Instance of the class
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Run the script to initialize the database
     *
     * @param connection Object that represents a connection to the database
     */
    private static void initializeDatabase(Connection connection) {
        try {
            InputStream is = Database.class.getResourceAsStream("/db.sql");
            assert is != null;
            RunScript.execute(connection, new InputStreamReader(is));
        } catch (Exception ex) {
            throw new RuntimeException("Database couldn't be initialized", ex);
        }
    }

    /**
     * Method to get a connection to the database
     *
     * @return A connection object
     * @throws SQLException In case of a database error
     */
    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);

        if (!isInitialized && connection != null) {
            initializeDatabase(connection);
            isInitialized = true;
        }

        return connection;
    }
}
