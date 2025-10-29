package com.java_web.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    // Configure via JVM system properties: -DB_URL=... -DDB_USER=... -DDB_PASS=...
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=JobBoard;encrypt=true;trustServerCertificate=true";
    private static final String USER = "sa";
    private static final String PASS = "YourStrong!Passw0rd";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQL Server JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
