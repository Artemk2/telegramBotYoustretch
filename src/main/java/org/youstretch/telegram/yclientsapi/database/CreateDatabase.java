package org.youstretch.telegram.yclientsapi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase {
    static final String HOST = "localhost";
    static final String PORT = "5432";
    static final String JDBC_DRIVER = "org.postgresql.Driver";
    static final String USER = "youstretch";
    static final String PASSWORD = "!QAZ2wsx";
    static final String TABLE_NAME = "EVENTS";
    private static final String DB_URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + USER;

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;

        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connecting to database was successful");

            //DROP TABLE TABLE_NAME
            System.out.println("DROP TABLE " + TABLE_NAME);
            statement = connection.createStatement();
            String dropTableSQL = "DROP TABLE " + TABLE_NAME;
            statement.executeUpdate(dropTableSQL);
            System.out.println("Database dropped successfully");

            // Execute a query
            System.out.println("Creating database...");
            String createTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                    "guid SERIAL PRIMARY KEY," +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "message TEXT," +
                    "status TEXT," +
                    "t_user TEXT)";
            statement.executeUpdate(createTableSQL);
            System.out.println("Database created successfully...");

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null)
                    statement.close();
            } catch (SQLException se2) {
            }
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        System.out.println("Goodbye!");
    }
}
