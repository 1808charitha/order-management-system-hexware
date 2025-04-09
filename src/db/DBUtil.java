package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ordermanagementsystem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Charitha@123";

    public static Connection getDBConn() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}