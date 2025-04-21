package Database;
import java.sql.*;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:scorekeeping.db";

    //Intialize the database
    public static void initialize() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
        Statement stmt = conn.createStatement()){
            //Teams Table
            String createTeamsTable = """
                CREATE TABLE IF NOT EXISTS Teams (
                    team_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_name TEXT NOT NULL,
                    coach_name TEXT
                );
            """;
            // Players Table
            String createPlayersTable = """
                CREATE TABLE IF NOT EXISTS Players (
                    player_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_id INTEGER,
                    player_name TEXT NOT NULL,
                    jersey_number INTEGER,
                    position TEXT,
                    FOREIGN KEY (team_id) REFERENCES Teams (team_id)
                );
            """;
            //Games Table
            String createGamesTable = """
                CREATE TABLE IF NOT EXISTS Games (
                    game_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    home_team_id INTEGER,
                    away_team_id INTEGER,
                    game_date TEXT,
                    status TEXT,
                    FOREIGN KEY (home_team_id) REFERENCES Teams (team_id),
                    FOREIGN KEY (away_team_id) REFERENCES Teams (team_id)
                );
            """;

            // Events Table
            String createEventsTable = """
                CREATE TABLE IF NOT EXISTS Events (
                    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    game_id INTEGER,
                    event_type TEXT,
                    team_id INTEGER,
                    player_id INTEGER,
                    points INTEGER,
                    timestamp TEXT,
                    FOREIGN KEY (game_id) REFERENCES Games (game_id),
                    FOREIGN KEY (team_id) REFERENCES Teams (team_id),
                    FOREIGN KEY (player_id) REFERENCES Players (player_id)
        
                );
            """;
            // Create Users table
            String createUsersTable = """
              CREATE TABLE IF NOT EXISTS Users (
              user_id INTEGER PRIMARY KEY AUTOINCREMENT,
              username TEXT UNIQUE NOT NULL,
              password TEXT NOT NULL,
              role TEXT DEFAULT 'scorekeeper'
               );
            """;

            // Execute table creation
            stmt.execute(createTeamsTable);
            stmt.execute(createPlayersTable);
            stmt.execute(createGamesTable);
            stmt.execute(createEventsTable);
            stmt.execute(createUsersTable);


            // Insert default admin user if no users exist
            String checkUsers = "SELECT COUNT(*) AS total FROM Users";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkUsers);
                 ResultSet rs = checkStmt.executeQuery()) {

                if (rs.next() && rs.getInt("total") == 0) {
                    String insertAdmin = "INSERT INTO Users (username, password, role) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertAdmin)) {
                        insertStmt.setString(1, "admin");          // username
                        insertStmt.setString(2, "admin123");       // plain text for now — hash later
                        insertStmt.setString(3, "admin");          // role
                        insertStmt.executeUpdate();
                        System.out.println("Default admin user created: admin / admin123");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            System.out.println("Database initialized and tables created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

