package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.Instructor;
import domain.Section;

public class Instructordatalayer {
    private int parseSemester(String sem) {
        try {
            return Integer.parseInt(sem.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
    }

    public static Instructor getInstructordetails(int user_id) {
        Instructor Instructor = null;
        try (Connection conn = DBConnection.getMainConnection()) {
            String query = "SELECT * FROM instructors WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, user_id);
            ResultSet rs = pstmt.executeQuery();
            try (Connection conn2 = DBConnection.getAuthConnection()) {
                String query2 = "SELECT username FROM users WHERE user_id = ?";
                PreparedStatement pstmt2 = conn2.prepareStatement(query2);
                pstmt2.setInt(1, user_id);
                ResultSet rs2 = pstmt2.executeQuery();

                if (rs.next() && rs2.next()) {
                    int UserID = rs2.getInt("user_id");
                    String username = rs2.getString("username");
                    String name = rs.getString("name");
                    String department = rs.getString("department");
                    Instructor = new Instructor(UserID, username, name, department);
                    return Instructor;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Instructor;
    }

    private Map<String, Integer> parseGradingJson(String json) {
        Map<String, Integer> map = new HashMap<>();
        json = json.replaceAll("[{}\"]", "");
        String[] entries = json.split(",");
        for (String entry : entries) {
            String[] kv = entry.split(":");
            if (kv.length == 2) {
                map.put(kv[0].trim(), Integer.parseInt(kv[1].trim()));
            }
        }
        return map;
    }

    private java.time.DayOfWeek mapDay(String shortDay) {
        switch (shortDay.toUpperCase()) {
            case "MON":
                return java.time.DayOfWeek.MONDAY;
            case "TUE":
                return java.time.DayOfWeek.TUESDAY;
            case "WED":
                return java.time.DayOfWeek.WEDNESDAY;
            case "THU":
                return java.time.DayOfWeek.THURSDAY;
            case "FRI":
                return java.time.DayOfWeek.FRIDAY;
            case "SAT":
                return java.time.DayOfWeek.SATURDAY;
            case "SUN":
                return java.time.DayOfWeek.SUNDAY;
            default:
                throw new IllegalArgumentException("Invalid day: " + shortDay);
        }
    }

    private Map<java.time.DayOfWeek, domain.TimeSlot> parseDayTime(String input) {
        Map<java.time.DayOfWeek, domain.TimeSlot> table = new HashMap<>();

        try {

            String[] parts = input.split(" ", 2);

            if (parts.length == 2) {
                String shortDay = parts[0].trim();

                java.time.DayOfWeek day = mapDay(shortDay);

                String timeStr = parts[1].trim();
                domain.TimeSlot slot = domain.TimeSlot.parse(timeStr);

                table.put(day, slot);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return table;
    }

    public List<Section> getSectionsByInstructorId(int instructorId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT section_id, course_id, instructor_id, day_time, room, " +
                "capacity, current_enrollment, semester, year, grading_scheme " +
                "FROM sections WHERE instructor_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int sectionId = rs.getInt("section_id");
                int courseId = rs.getInt("course_id");
                String room = rs.getString("room");
                int semester = parseSemester(rs.getString("semester"));
                int year = rs.getInt("year");
                int capacity = rs.getInt("capacity");
                int currentEnrollment = rs.getInt("current_enrollment");
                String instructorid = rs.getString("instructor_id");
                Section s = new Section(
                        courseId,
                        instructorid,
                        String.valueOf(sectionId),
                        room,
                        semester,
                        year,
                        capacity,
                        null);

                s.setCurrentEnrollment(currentEnrollment);

                
                String gradingJson = rs.getString("grading_scheme");
                if (gradingJson != null && !gradingJson.isEmpty()) {
                    s.setGradingMap(parseGradingJson(gradingJson));
                }

             
                String dayTime = rs.getString("day_time");
                if (dayTime != null && !dayTime.isEmpty()) {
                    s.setTimeTable(parseDayTime(dayTime));
                }

                sections.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;
    }

    public int getInstructorIdByUserId(int userId) {
        int instructorId = -1;
        String sql = "SELECT instructor_id FROM instructors WHERE user_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                instructorId = rs.getInt("instructor_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructorId;
    }

    public List<Integer> getEnrolledStudentIds(int sectionId) {
        List<Integer> students = new ArrayList<>();
        String sql = "SELECT student_id FROM enrollments WHERE section_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                students.add(rs.getInt("student_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
    }

    public int getInstructorIdBySectionId(int sectionId) {
        int instructorId = -1;
        String sql = "SELECT instructor_id FROM sections WHERE section_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                instructorId = rs.getInt("instructor_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructorId;
    }

    public String getInstructorNameById(int instructorId) {
        String instructorName = null;
        String sql = "SELECT name FROM instructors WHERE instructor_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                instructorName = rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructorName;

    }

    public static void main(String[] args) {
        Instructordatalayer idl = new Instructordatalayer();
        List<Section> sections = idl.getSectionsByInstructorId(33);
        for (Section s : sections) {
            System.out.println("Section ID: " + s.getSectionCapacity() + ", Course ID: " + s.getCourseID());
            System.out.println("Grading Scheme: " + s.getGradingMap());
            System.out.println("Timetable: " + s.getTimeTable());
            System.out.println("-----");

        }

    }

    public int getUserByUsername(String Username) {
        String sql = "SELECT user_id FROM users_auth WHERE username = ?";
        int userid = -1;
        try (Connection conn = DBConnection.getAuthConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, Username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userid = rs.getInt("user_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userid;
    }

    public int getInstructorIdFromUsername(String username) {
        int userId = getUserByUsername(username);
        int instructorId = -1;
        String sql = "SELECT instructor_id FROM instructors WHERE user_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                instructorId = rs.getInt("instructor_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instructorId;

    }

}