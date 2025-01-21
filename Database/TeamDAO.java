package Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TeamDAO {
    // Add a team
    public void addTeam(String teamName, String coachName) {
        String sql = "INSERT INTO Teams (team_name, coach_name) VALUES (?, ?)";
        try (Connection conn = Database.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, teamName);
                pstmt.setString(2, coachName);
                pstmt.executeUpdate();
                System.out.println("Team added successfully.");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Retrieve all teams
    public void getAllTeams() {
        String sql = "SELECT * FROM Teams";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("Team ID: " + rs.getInt("team_id") +
                        ", Name: " + rs.getString("team_name") +
                        ", Coach: " + rs.getString("coach_name"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


