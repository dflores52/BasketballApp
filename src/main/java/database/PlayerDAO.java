package database;
import model.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    public void addPlayer(Player player, int teamId) throws SQLException {
        String sql = "INSERT INTO Players (player_name, jersey_number, team_id) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, player.getPlayerName());
            pstmt.setInt(2, player.getJerseyNumber());
            pstmt.setInt(3, teamId);
            pstmt.executeUpdate();
        }
    }
    public List<Player> getPlayersByTeamId(int teamId) throws SQLException {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM Players WHERE team_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, teamId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String playerName = rs.getString("player_name");
                    int jerseyNumber = rs.getInt("jersey_number");
                    Player player = new Player(playerName, jerseyNumber);
                    players.add(player);
                }
            }
        }

        return players;
    }
    public void updatePlayer(Player player) throws SQLException {
        String sql = "UPDATE Players SET player_name = ? WHERE jersey_number = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, player.getPlayerName());
            pstmt.setInt(2, player.getJerseyNumber());
            pstmt.executeUpdate();
        }
    }
    public void deletePlayer(int jerseyNumber, int teamId) throws SQLException {
        String sql = "DELETE FROM Players WHERE jersey_number = ? AND team_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, jerseyNumber);
            pstmt.setInt(2, teamId);
            pstmt.executeUpdate();
        }
    }




}