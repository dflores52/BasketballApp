package Database;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class PlayerDAO {
    public void addPlayer(String playerName, int teamId, int jerseyNumber, String position) {
        String sql = "INSERT INTO Players (player_name, team_id, jersey_number, position) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, playerName);
            pstmt.setInt(2, teamId);
            pstmt.setInt(3, jerseyNumber);
            pstmt.setString(4, position);
            pstmt.executeUpdate();
            System.out.println("Player added successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
