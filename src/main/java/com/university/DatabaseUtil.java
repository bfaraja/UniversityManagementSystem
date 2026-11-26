package com.university;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    private static final String URL =
            "jdbc:mysql://localhost:3306/university_db?useSSL=false&serverTimezone=UTC";

    private static final String USER = "root";

    private static final String PASSWORD =
            System.getenv("MYSQL_PASSWORD");

    public static Connection getConnection() throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "MYSQL_PASSWORD environment variable is not set."
            );
        }

        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean testConnection() {

        try (Connection connection = getConnection()) {
            return connection != null && connection.isValid(2);

        } catch (SQLException e) {
            System.out.println(
                    "Database connection failed: " + e.getMessage()
            );
            return false;
        }
    }
}