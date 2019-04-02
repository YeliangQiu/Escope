package eScope;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    public static Connection getConn() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/pastehtml?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC", "root", "MyNewPass4!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
