package Database;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        // Initialize the database
        Database.initialize();

        // Test TeamDAO
        TeamDAO teamDAO = new TeamDAO();
        teamDAO.addTeam("Team A", "Coach John");
        teamDAO.addTeam("Team B", "Coach Jane");

        System.out.println("\nTeams in Database:");
        teamDAO.getAllTeams();

        // Test PlayerDAO
        PlayerDAO playerDAO = new PlayerDAO();
        playerDAO.addPlayer("John Doe", 1, 23, "Guard");
        playerDAO.addPlayer("Jane Smith", 2, 45, "Forward");

        System.out.println("\nPlayers added successfully.");
    }
}
