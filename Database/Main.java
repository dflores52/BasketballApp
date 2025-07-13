package Database;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Your application code
        
        // Register shutdown hook to close connection pool
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Database.closeConnection();
        }));
    }
}