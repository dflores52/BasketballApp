package Database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
            // Execute table creation
            stmt.execute(createTeamsTable);
            stmt.execute(createPlayersTable);
            stmt.execute(createGamesTable);
            stmt.execute(createEventsTable);

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

