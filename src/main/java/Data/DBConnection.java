package Data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import setup.DBConfig;

public class DBConnection {
    private static final String AUTH_URL = "jdbc:mysql://localhost:3306/erp_auth";
    private static final String MAIN_URL = "jdbc:mysql://localhost:3306/erp_main";
    private static final String USER = "root";
    private static final String PASS = DBConfig.pass;

    public static Connection getAuthConnection() throws SQLException {
        return DriverManager.getConnection(AUTH_URL, USER, PASS);
    }

    public static Connection getMainConnection() throws SQLException {
        return DriverManager.getConnection(MAIN_URL, USER, PASS);
    }
}