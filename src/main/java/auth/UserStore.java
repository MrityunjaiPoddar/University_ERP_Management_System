package auth;

import java.sql.*;

import setup.DBConfig;

public class UserStore {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/erp_auth";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = DBConfig.pass;

    public static boolean verifyUser(String username, String plainPassword) {
        String query = "SELECT password_hash FROM users_auth WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return authhash.checkPassword(plainPassword, storedHash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getUserRole(String username) {
        String query = "SELECT role_ FROM users_auth WHERE username=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getString("role_");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getUserId(String username) {
        String query = "SELECT user_id FROM users_auth WHERE username=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt("user_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getFailedAttempts(String username) {
        String sql = "SELECT failed_attempts FROM users_auth WHERE username=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getInt("failed_attempts");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getLockoutUntil(String username) {
        String sql = "SELECT lockout_until FROM users_auth WHERE username=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                return rs.getLong("lockout_until");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void updateFailedAttempts(String username, int attempts) {
        String sql = "UPDATE users_auth SET failed_attempts=? WHERE username=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, attempts);
            stmt.setString(2, username);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void lockUser(String username, long lockoutUntil) {
        String sql = "UPDATE users_auth SET failed_attempts=?, lockout_until=? WHERE username=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, 5); // failed attempts stays 5
            stmt.setLong(2, lockoutUntil);
            stmt.setString(3, username);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void resetLockout(String username) {
        String sql = "UPDATE users_auth SET failed_attempts=0, lockout_until=0 WHERE username=?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}