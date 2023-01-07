package com.example.order.util;

import org.h2.tools.RunScript;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to get database connections
 *
 * @link https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
 */
public class Database {
    private static Database instance = null;
    private static boolean isInitialized = false;

    private static final String url = "jdbc:h2:mem:orders;DB_CLOSE_DELAY=-1";
    private static final String user = "sa";
    private static final String password = "";

    /**
     * Private constructor
     */
    private Database() {
    }

    private static class LazyHolder {
        static final Database INSTANCE = new Database();
    }

    /**
     * Method that creates an instance of this class (if it's not created already)
     *
     * @return Instance of the class
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = LazyHolder.INSTANCE;
        }
        return instance;
    }

    /**
     * Run the script to initialize the database
     *
     * @param connection Object that represents a connection to the database
     */
    private static void initializeDatabase(Connection connection) throws IOException {
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
    public Connection getConnection() throws SQLException, IOException {
        Connection connection = DriverManager.getConnection(url, user, password);

        if (!isInitialized && connection != null) {
            initializeDatabase(connection);
            isInitialized = true;
        }

        return connection;
    }
}
