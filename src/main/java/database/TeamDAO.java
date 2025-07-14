package database;
import model.Team;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {
    public void addTeam(String teamName, String coachName) throws SQLException {
        String sql = "INSERT INTO Teams (team_name, coach_name) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teamName);
            pstmt.setString(2, coachName);
            pstmt.executeUpdate();
        }
    }

    public List<Team> getAllTeams() throws SQLException {
        List<Team> teams = new ArrayList<>();
        String sql = "SELECT * FROM Teams";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                teams.add(new Team(
                    rs.getInt("team_id"),
                    rs.getString("team_name"),
                    rs.getString("coach_name")
                ));
            }
        }
        return teams;
    }

    public void updateTeam(Team team) throws SQLException {
        String sql = "UPDATE Teams SET team_name = ?, coach_name = ? WHERE team_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, team.getTeamName());
            pstmt.setString(2, team.getCoachName());
            pstmt.setInt(3, team.getTeamId());
            pstmt.executeUpdate();
        }
    }

    public void deleteTeam(int teamId) throws SQLException {
        String sql = "DELETE FROM Teams WHERE team_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, teamId);
            pstmt.executeUpdate();
        }
    }
}