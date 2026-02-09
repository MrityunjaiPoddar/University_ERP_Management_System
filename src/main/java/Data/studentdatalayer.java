package Data;

import java.sql.*;
import domain.*;

public class studentdatalayer {
    public static String getusernamefromuserid(int userId) {
        String username = null;
        String sql = "SELECT username FROM users_auth WHERE user_id = ?";
        try (Connection conn = DBConnection.getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                username = rs.getString("username");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    public static Student getstudentdetails(int student_id) {
        String sql = "SELECT student_id, user_id, name, roll_no, program, department, semester, cgpa, year FROM student WHERE student_id = ?";
        Student student = null;
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, student_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String username = getusernamefromuserid(rs.getInt("user_id"));
                student = new Student(
                        rs.getInt("user_id"),
                        username,
                        rs.getString("name"),
                        rs.getInt("roll_no"),
                        rs.getString("program"),
                        rs.getInt("year"));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public int getUserIdByUsername(String username) {
        int userid = -1;
        try (Connection conn = DBConnection.getAuthConnection()) {
            String sql = "SELECT u.user_id FROM users_auth u WHERE u.username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        userid = rs.getInt("user_id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userid;
    }

    public int getstudentIdfromusername(String username) {
        int userid = getUserIdByUsername(username);
        String sql = "SELECT student_id FROM student WHERE user_id=?";
        try (Connection conn = DBConnection.getMainConnection()) {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("student_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updateCGPA(int studentId, double newCgpa) {
        try (Connection conn = DBConnection.getMainConnection()) {
            String sql = "UPDATE student SET cgpa = ? WHERE student_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, newCgpa);
                ps.setInt(2, studentId);
                int rows = ps.executeUpdate();
                return rows > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getStudentSemester(int studentId1) {
        int semester = -1;
        String sql = "SELECT semester FROM student WHERE student_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId1);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                semester = rs.getInt("semester");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return semester;
    }

}
