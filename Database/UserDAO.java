package Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public boolean login(String username, String password) throws SQLException {
        String sql = "select * from users where username=? and password=?";
        try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, username);
            pstmt.setString(2,password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // returns true if a match is found
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public void register(String username, String password, String role) throws SQLException {
        String sql = "insert into users (username, password, role) values(?,?,?)";
        try (Connection conn = Database.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);
            pstmt.executeUpdate();

        } catch(Exception e){
            e.printStackTrace();

        }
    }
}
