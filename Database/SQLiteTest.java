package Database;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class SQLiteTest {

    public static void main(String[] args) throws SQLException {
        String url = "jdc:sqlite:test.db";

        try(Connection conn = DriverManager.getConnection(url)){
            if(conn != null){
                System.out.println("Connected to SQLite");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}
