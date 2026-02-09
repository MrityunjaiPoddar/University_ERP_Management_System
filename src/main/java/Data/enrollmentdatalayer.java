package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import domain.Student;

public class enrollmentdatalayer {
    public boolean hasReachedCreditLimit(int studentId) {

        int totalCredits = 0;

        try (Connection conn = DBConnection.getMainConnection()) {

            int studentSem = getStudentSemester(studentId);

            String q1 = "SELECT section_id FROM enrollments WHERE student_id = ? AND status = 'enrolled'";
            PreparedStatement ps1 = conn.prepareStatement(q1);
            ps1.setInt(1, studentId);
            ResultSet rs1 = ps1.executeQuery();

            while (rs1.next()) {

                int sectionId = rs1.getInt("section_id");

                int sectionSem = getSectionSemester(sectionId);

                if (sectionSem != studentSem)
                    continue; // skip credits from previous semesters

                String q2 = "SELECT course_id FROM sections WHERE section_id = ?";
                PreparedStatement ps2 = conn.prepareStatement(q2);
                ps2.setInt(1, sectionId);
                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {
                    int courseId = rs2.getInt("course_id");
                    String q3 = "SELECT credits FROM courses WHERE course_id = ?";
                    PreparedStatement ps3 = conn.prepareStatement(q3);
                    ps3.setInt(1, courseId);
                    ResultSet rs3 = ps3.executeQuery();

                    if (rs3.next()) {
                        totalCredits += rs3.getInt("credits");

                        if (totalCredits >= 20) {
                            return true;
                        }
                    }

                    rs3.close();
                    ps3.close();
                }

                rs2.close();
                ps2.close();
            }

            rs1.close();
            ps1.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalCredits >= 20;
    }

    public int getCourseIdFromSection(int sectionId) {
        String sql = "SELECT course_id FROM sections WHERE section_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("course_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean isStudentAlreadyInCourse(int studentId, int courseId) {

        try (Connection conn = DBConnection.getMainConnection()) {

            String sql1 = "SELECT section_id FROM enrollments WHERE student_id = ? AND status = 'enrolled'";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, studentId);
            ResultSet rs1 = ps1.executeQuery();

            while (rs1.next()) {
                int sectionId = rs1.getInt("section_id");

                String sql2 = "SELECT course_id FROM sections WHERE section_id = ?";
                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, sectionId);
                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {
                    int cid = rs2.getInt("course_id");

                    if (cid == courseId) {
                        return true;
                    }
                }
                rs2.close();
                ps2.close();
            }

            rs1.close();
            ps1.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getcoursestatusfromsectionid(int studentId, int sectionId) {
        String status = null;

        try (Connection conn = DBConnection.getMainConnection()) {

            String sql = "SELECT status FROM enrollments WHERE student_id = ? AND section_id = ? ORDER BY enrollment_id DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                status = rs.getString("status");

                rs.close();
                ps.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public String getCourseStatus(int studentId, int courseId) {

        String status = null;

        try (Connection conn = DBConnection.getMainConnection()) {

            String sql1 = "SELECT section_id FROM sections WHERE course_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, courseId);
            ResultSet rs1 = ps1.executeQuery();

            while (rs1.next()) {
                int sectionId = rs1.getInt("section_id");
                String sql2 = "SELECT status FROM enrollments " +
                        "WHERE student_id = ? AND section_id = ? " +
                        "ORDER BY enrollment_id DESC LIMIT 1";

                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, studentId);
                ps2.setInt(2, sectionId);

                ResultSet rs2 = ps2.executeQuery();

                if (rs2.next()) {
                    status = rs2.getString("status"); 
                    rs2.close();
                    ps2.close();
                    break;
                }

                rs2.close();
                ps2.close();
            }

            rs1.close();
            ps1.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return status;
    }

    public Integer getDroppedSectionId(int studentId, int courseId) {

        String sql = "SELECT section_id FROM enrollments " +
                "WHERE student_id=? AND status='dropped'";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int secId = rs.getInt("section_id");
                if (getCourseIdFromSection(secId) == courseId) {
                    return secId;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public int registerStudentInSection(int studentId, int sectionId) {

        if (hasReachedCreditLimit(studentId)) {
            return 2;
        }

        int newCourseId = getCourseIdFromSection(sectionId);
        String status = getCourseStatus(studentId, newCourseId);

        if ("enrolled".equalsIgnoreCase(status)) {
            System.out.println(status);
            System.out.println("Student already enrolled in this course.");
            return 3;
        }

        if ("dropped".equalsIgnoreCase(status)) {
            Integer droppedSection = getDroppedSectionId(studentId, newCourseId);
            if (droppedSection == null) {
                System.out.println("Dropped record NOT FOUND");
                return 0;
            }

            try (Connection conn = DBConnection.getMainConnection()) {
                if (droppedSection == sectionId) {
                    String sql = "UPDATE enrollments SET status='enrolled' " +
                            "WHERE student_id=? AND section_id=?";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, studentId);
                    ps.setInt(2, sectionId);
                    ps.executeUpdate();

                    String sql2 = "UPDATE sections SET current_enrollment = current_enrollment + 1 WHERE section_id=?";
                    PreparedStatement ps2 = conn.prepareStatement(sql2);
                    ps2.setInt(1, sectionId);
                    ps2.executeUpdate();

                    return 1;
                }
                String sqlNew = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'enrolled')";
                PreparedStatement ps3 = conn.prepareStatement(sqlNew);
                ps3.setInt(1, studentId);
                ps3.setInt(2, sectionId);
                ps3.executeUpdate();
                String sql2 = "UPDATE sections SET current_enrollment = current_enrollment + 1 WHERE section_id=?";
                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, sectionId);
                ps2.executeUpdate();
                return 1;

            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
        int studentSem = getStudentSemester(studentId);
        int sectionSem = getSectionSemester(sectionId);
        System.out.println("Semester mismatch: student sem " + studentSem + ", section sem " + sectionSem);

        if (studentSem != -1 && sectionSem != -1 && studentSem != sectionSem) {
            System.out.println("Semester mismatch: student sem " + studentSem + ", section sem " + sectionSem);
            return 4;
        }

        String sql = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, 'enrolled')";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ps.executeUpdate();
            return 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getSectionSemester(int sectionId) {
        String sql = "SELECT semester FROM sections WHERE section_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String semStr = rs.getString("semester").trim();

                try {
                    return Integer.parseInt(semStr);
                } catch (NumberFormatException ignored) {
                }

                if (semStr.toLowerCase().startsWith("sem")) {
                    String numPart = semStr.substring(3);
                    try {
                        return Integer.parseInt(numPart);
                    } catch (NumberFormatException ignored) {
                    }
                }
                return -1;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getStudentSemester(int studentId) {
        String sql = "SELECT semester FROM student WHERE student_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("semester");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean dropStudentFromSection(int studentId, int sectionId) {
        String sql = "UPDATE enrollments SET status = 'dropped' " +
                "WHERE student_id = ? AND section_id = ? AND status = 'enrolled'";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getEnrollmentId(int studentId, int sectionId) {
        String sql = "SELECT enrollment_id FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setInt(2, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("enrollment_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<Integer> getStudentsInSection(int sectionId) {
        List<Integer> studentIds = new ArrayList<>();
        String sql = "SELECT student_id FROM enrollments WHERE section_id = ? AND status = 'enrolled'";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                studentIds.add(rs.getInt("student_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return studentIds;
    }

    public List<Student> getStudentDetails(int sectionId) {
        List<Student> students = new ArrayList<>();

        List<Integer> studentIds = getStudentsInSection(sectionId);

        for (Integer studentId : studentIds) {
            new studentdatalayer();
            Student student = studentdatalayer.getstudentdetails(studentId);

            if (student != null) {
                students.add(student);
            }
        }

        return students; 
    }

    public int getStudentIdFromRoll(String roll) {
        String sql = "SELECT student_id FROM student WHERE roll_no = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, roll);
            ResultSet rs = ps.executeQuery();

            if (rs.next())
                return rs.getInt("student_id");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getEnrollmentIdFromStudentAndSection(int int1, int secId) {
        String sql = "SELECT enrollment_id FROM enrollments WHERE student_id = ? AND section_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, int1);
            ps.setInt(2, secId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("enrollment_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
