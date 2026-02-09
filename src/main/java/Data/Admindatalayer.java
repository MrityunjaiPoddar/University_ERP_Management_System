package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import auth.authhash;
import domain.Admin;
import domain.Course;
import domain.Instructor;
import domain.Section;
import domain.Student;
import domain.User;

public class Admindatalayer {
    private int insertUserAuth(Connection conn, String username, String role, String password) throws SQLException {

        String sql = "INSERT INTO users_auth (username, role_, password_hash) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            String hashed = authhash.hashPassword(password);
            ps.setString(1, username);
            ps.setString(2, role.toLowerCase());
            ps.setString(3, hashed);

            int rows = ps.executeUpdate();
            if (rows == 0)
                return -1;

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean addStudentUser(String username, String password,
            String name, int rollNumber, String program, String Department, int semester, int year) {

        try (Connection conn = DBConnection.getAuthConnection()) {
            int userId = insertUserAuth(conn, username, "user", password);
            if (userId == -1) {
                conn.rollback();
                return false;
            }

            String sql = "INSERT INTO student (user_id, name, roll_no, program, department, semester, year) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn1 = DBConnection.getMainConnection();) {
                PreparedStatement ps = conn1.prepareStatement(sql);
                ps.setInt(1, userId);
                ps.setString(2, name);
                ps.setInt(3, rollNumber);
                ps.setString(4, program);
                ps.setString(5, Department);
                ps.setInt(6, semester);
                ps.setInt(7, year);
                ps.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addInstructorUser(String username, String password,
            String name, String department, String designation) {

        try (Connection conn = DBConnection.getAuthConnection()) {
            int userId = insertUserAuth(conn, username, "instructor", password);
            if (userId == -1) {
                conn.rollback();
                return false;
            }

            String sql = "INSERT INTO instructors (user_id, department, designation, name) VALUES (?, ?, ?, ?)";

            try (Connection conn2 = DBConnection.getMainConnection();
                    PreparedStatement ps = conn2.prepareStatement(sql)) {

                ps.setInt(1, userId);
                ps.setString(2, department);
                ps.setString(3, designation);
                ps.setString(4, name);
                ps.executeUpdate();
            }
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addAdminUser(String username, String password) {

        try (Connection conn = DBConnection.getAuthConnection()) {
            int userId = insertUserAuth(conn, username, "admin", password);

            if (userId == -1) {
                conn.rollback();
                System.out.println("Failed to insert admin user in users_auth.");
                return false;
            }

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, role_ FROM users_auth";

        try (Connection conn = DBConnection.getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("user_id");
                String username = rs.getString("username");
                String role = rs.getString("role_");
                users.add(new User(id, username, role));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    public User getUserByUsername(String username) {

        String sql = "SELECT user_id, username, role_, password_hash FROM users_auth WHERE username = ?";

        try (Connection conn = DBConnection.getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("user_id");
                String uname = rs.getString("username");
                String role = rs.getString("role_");
                String hashedPass = rs.getString("password_hash");

                User user = new User(id, uname, role);
                user.setHashedPassword(hashedPass);

                return user;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Student getStudentByRollNo(String rollNo) {

        Integer userId = null;
        String sqlMain = "SELECT user_id, name, program, department, semester, cgpa, year " +
                "FROM student WHERE roll_no = ?";

        String name = null, program = null, department = null;
        int semester = 0, year = 0;
        double cgpa = 0;

        try (Connection connMain = DBConnection.getMainConnection();
                PreparedStatement ps = connMain.prepareStatement(sqlMain)) {

            ps.setString(1, rollNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userId = rs.getInt("user_id");
                name = rs.getString("name");
                program = rs.getString("program");
                department = rs.getString("department");
                semester = rs.getInt("semester");
                cgpa = rs.getDouble("cgpa");
                year = rs.getInt("year");
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        String username = null;

        String sqlAuth = "SELECT username FROM users_auth WHERE user_id = ?";

        try (Connection connAuth = DBConnection.getAuthConnection();
                PreparedStatement ps2 = connAuth.prepareStatement(sqlAuth)) {

            ps2.setInt(1, userId);
            ResultSet rs2 = ps2.executeQuery();

            if (rs2.next()) {
                username = rs2.getString("username");
            } else {
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        Student student = new Student(
                userId,
                username,
                name,
                Integer.parseInt(rollNo),
                program,
                year);

        student.setCGPA(cgpa);
        student.setDepartment(department);
        student.setSemester(semester);

        return student;
    }

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();

        String sql = "SELECT roll_no FROM student";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String roll = rs.getString("roll_no");
                Student s = getStudentByRollNo(roll);
                list.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Instructor> getInstructorsByName(String name) {

        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT user_id, name, department, designation FROM instructors WHERE name LIKE ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int userId = rs.getInt("user_id");
                String instName = rs.getString("name");
                String dept = rs.getString("department");
                String designation = rs.getString("designation");

                String username = null;
                String sqlAuth = "SELECT username FROM users_auth WHERE user_id = ?";

                try (Connection connAuth = DBConnection.getAuthConnection();
                        PreparedStatement ps2 = connAuth.prepareStatement(sqlAuth)) {

                    ps2.setInt(1, userId);
                    ResultSet rs2 = ps2.executeQuery();

                    if (rs2.next()) {
                        username = rs2.getString("username");
                    }
                }

                Instructor instructor = new Instructor(userId, username, instName, dept);
                instructor.setDesignation(designation);

                instructors.add(instructor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return instructors;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT user_id, username FROM users_auth WHERE role_ = 'admin'";

        try (Connection conn = DBConnection.getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                Admin admin = new Admin(userId, "admin", username);
                admins.add(admin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    public List<Admin> getAdminsByName(String name) {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT user_id, username FROM users_auth WHERE role_ = 'admin' AND username LIKE ?";

        try (Connection conn = DBConnection.getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                Admin admin = new Admin(userId, "admin", username);
                admins.add(admin);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }

    public List<Instructor> getAllInstructors() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT user_id, name, department, designation FROM instructors";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String name = rs.getString("name");
                String department = rs.getString("department");
                String designation = rs.getString("designation");
                String sqlAuth = "SELECT username FROM users_auth WHERE user_id = ?";
                String username = null;
                try (Connection connAuth = DBConnection.getAuthConnection();
                        PreparedStatement ps2 = connAuth.prepareStatement(sqlAuth)) {

                    ps2.setInt(1, userId);
                    ResultSet rs2 = ps2.executeQuery();

                    if (rs2.next()) {
                        username = rs2.getString("username");
                    } else {
                        return null;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
                Instructor instructor = new Instructor(userId, username, name, department);
                instructor.setDesignation(designation);
                instructors.add(instructor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructors;
    }

    public boolean assignInstructorToSection(int sectionId, int instructorId) {
        String sql = "UPDATE sections SET instructor_id = ? WHERE section_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.setInt(2, sectionId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean createCourse(Course course) {
        coursedatalayer dao = new coursedatalayer();
        return dao.addCourse(course);
    }

    public boolean deleteCourse(int courseID) {
        coursedatalayer dao = new coursedatalayer();
        return dao.deleteCourse(courseID);
    }

    public boolean createSection(Section section) {
        sectiondatalayer dao = new sectiondatalayer();
        return dao.addSection(section);
    }

    public boolean deleteSection(int sectionID) {
        sectiondatalayer dao = new sectiondatalayer();
        return dao.deleteSection(sectionID);
    }

    public boolean toggleMaintenanceMode(boolean status) {
        settingsdatalayer settingsDAO = new settingsdatalayer();
        settingsDAO.toggleMaintenanceMode(status);
        return true;
    }
}