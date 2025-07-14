package database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerDAO {
    public void addPlayer(String playerName, int teamId, int jerseyNumber, String position) throws SQLException {
        // Input validation
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new IllegalArgumentException("model.Player name cannot be empty");
        }
        if (jerseyNumber < 0 || jerseyNumber > 99) {
            throw new IllegalArgumentException("Invalid jersey number");
        }
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position cannot be empty");
        }

        String sql = "INSERT INTO Players (player_name, team_id, jersey_number, position) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.setInt(2, teamId);
            pstmt.setInt(3, jerseyNumber);
            pstmt.setString(4, position);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            // Log the error properly
            throw new SQLException("Error adding player: " + e.getMessage(), e);
        }
    }
}