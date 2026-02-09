package Data;

import domain.Course;
import java.sql.*;
import java.util.*;

public class coursedatalayer {

    public List<Course> getCoursesBySemester(int semester) {
        List<Course> courses = new ArrayList<>();

        try (Connection conn = DBConnection.getMainConnection()) {
            String sql = "SELECT code, title, credits, capacity FROM courses WHERE semester = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, semester);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String code = rs.getString("code");
                String title = rs.getString("title");
                int credits = rs.getInt("credits");
                courses.add(new Course(code, title, credits));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public Course getCourseByCode(String courseCode) {
        Course course = null;
        String sql = "SELECT * FROM courses WHERE code = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                course = new Course(
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return course;
    }

    public int getCourseIdByCode(String courseCode) {
        int courseId = -1;
        String sql = "SELECT course_id FROM courses WHERE code = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                courseId = rs.getInt("course_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courseId;
    }

    public Course getcoursebycourseid(int courseId) {
        Course course = null;
        String sql = "SELECT * FROM courses WHERE course_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                course = new Course(
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }

    public int sectionidByCourseCode(String courseCode) {
        int sectionId = -1;
        int a = getCourseIdByCode(courseCode);
        String sql = "SELECT section_id FROM sections WHERE course_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, a);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                sectionId = rs.getInt("section_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sectionId;
    }

    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Course course = new Course(
                        rs.getString("code"),
                        rs.getString("title"),
                        rs.getInt("credits"));
                courses.add(course);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return courses;

    }

    public boolean addCourse(Course course) {
        String sql = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, course.getCode());
            ps.setString(2, course.getTitle());
            ps.setInt(3, course.getCredits());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateCourse(String coursetitle, int credits, String coursecode) {
        String sql = "UPDATE courses SET title = ?, credits = ? WHERE code = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, coursetitle);
            ps.setInt(2, credits);
            ps.setString(3, coursecode);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, courseId);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}