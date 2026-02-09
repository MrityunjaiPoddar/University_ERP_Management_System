package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class settingsdatalayer {
    public boolean isMaintenanceOn() {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = 'maintenance_mode'";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return Boolean.parseBoolean(rs.getString("setting_value"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void toggleMaintenanceMode(boolean status) {
        String sql = "UPDATE settings SET setting_value = ? WHERE setting_key = 'maintenance_mode'";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, String.valueOf(status));
            int rows = ps.executeUpdate();
            if (rows == 0) {
                String insertSql = "INSERT INTO settings (setting_key, setting_value) VALUES ('maintenance_mode', ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(insertSql)) {
                    ps2.setString(1, String.valueOf(status));
                    ps2.executeUpdate();
                }
            }

            System.out.println("Maintenance mode: " + (status ? "ON" : "OFF"));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSetting(String key, String value) {
        String sql = "UPDATE settings SET setting_value = ? WHERE setting_key = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, value);
            ps.setString(2, key);
            int rows = ps.executeUpdate();

            
            if (rows == 0) {
                String insert = "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?)";
                try (PreparedStatement ps2 = conn.prepareStatement(insert)) {
                    ps2.setString(1, key);
                    ps2.setString(2, value);
                    ps2.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}