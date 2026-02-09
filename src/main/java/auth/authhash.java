package auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import Data.DBConnection;

public class authhash {
    public static String hashPassword(String plainPassword) {
        int rounds = java.util.concurrent.ThreadLocalRandom.current().nextInt(1, 14);
        if (rounds < 4)
            rounds = 4;
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(rounds));
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null)
            return false;
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static void printPassword(String plainPassword) {
        if (plainPassword == null) {
            System.out.println("Password: null");
            return;
        }
        String masked = plainPassword.replaceAll(".", "*");
        System.out.println("Password (masked): " + masked + " (length=" + plainPassword.length() + ")");
        System.out.println("Password hash: " + hashPassword(plainPassword));
    }

    public static void printOriginalPasswordFromHash(String hashedPassword) {
        if (hashedPassword == null) {
            System.out.println("Hashed password: null");
            return;
        }
        System.out.println("Cannot retrieve the original password from a bcrypt hash.");
        System.out.println(
                "Bcrypt is a one-way hash; use checkPassword(plainPassword, hashedPassword) to verify a candidate,");
        System.out.println("or implement a secure password reset flow if the original is lost.");
    }

    public String getHashedPassword(int userId) {
        String sql = "SELECT password_hash FROM users_auth WHERE user_id = ?";

        try (Connection conn = DBConnection.getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("password_hash");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(int userId, String newHashedPassword) {
        String sql = "UPDATE users_auth SET password_hash = ? WHERE user_id = ?";

        try (Connection conn = DBConnection.getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newHashedPassword);
            ps.setInt(2, userId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changePassword(int userId, String oldPass, String newPass) {

        String oldHash = getHashedPassword(userId);
        if (oldHash == null)
            return false;

        if (!authhash.checkPassword(oldPass, oldHash)) {
            return false;
        }

        String newHash = authhash.hashPassword(newPass);

        return updatePassword(userId, newHash);
    }
}
