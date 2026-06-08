
import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcExample1 {
    public static void main(String[] args) {
        // Load driver (optional on modern JDBC)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ignore) {}

        // Use your task database instead of testdb
        String url = "jdbc:mysql://localhost:3306/task?useSSL=false&serverTimezone=UTC";
        String user = "root";   // same as spring.datasource.username
        String pass = "Divya@12";   // same as spring.datasource.password

        try (Connection con = DriverManager.getConnection(url, user, pass)) {
            System.out.println("Connected to Database!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}