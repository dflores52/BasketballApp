package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:scorekeeping.db";
    private static Connection connection;

    public static void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Drop the existing table to ensure we have the correct schema
            stmt.execute("DROP TABLE IF EXISTS Users");

            // Create Users table with the new schema
            stmt.execute("CREATE TABLE Users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL," +
                    "salt VARCHAR(255) NOT NULL," +
                    "role VARCHAR(20) NOT NULL," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "last_login DATETIME," +
                    "CHECK (role IN ('ADMIN', 'COACH', 'SCORER', 'USER'))" +
                    ")");
        }
    }


    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
            // Enable foreign keys for SQLite
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}