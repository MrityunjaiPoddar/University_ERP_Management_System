package Data;

import domain.Section;
import domain.TimeSlot;
import com.google.gson.Gson;
import java.sql.*;
import java.util.*;

public class sectiondatalayer {
    private int parseSemester(String sem) {
        try {
            return Integer.parseInt(sem.replaceAll("[^0-9]", ""));
        } catch (Exception e) {
            return 0;
        }
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

    private String convertTimeTableToString(Map<java.time.DayOfWeek, TimeSlot> map) {
        if (map == null || map.isEmpty())
            return "";

        StringBuilder sb = new StringBuilder();

        for (var entry : map.entrySet()) {

            String shortDay = entry.getKey().name().substring(0, 3);
            shortDay = shortDay.charAt(0) + shortDay.substring(1).toLowerCase();

            sb.append(shortDay) 
                    .append(" ")
                    .append(entry.getValue().toString()) 
                    .append(",");
        }

        return sb.substring(0, sb.length() - 1); 
    }

    private String convertGradingMapToJson(Map<String, Integer> map) {
        if (map == null || map.isEmpty())
            return "{}";
        StringBuilder sb = new StringBuilder("{");
        for (var entry : map.entrySet()) {
            sb.append("\"").append(entry.getKey()).append("\":").append(entry.getValue()).append(",");
        }
        sb.setLength(sb.length() - 1); 
        sb.append("}");
        return sb.toString();
    }

    public List<Section> getSectionsBySemester(String semester) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT section_id, course_id, instructor_id, day_time, room, " +
                "capacity, current_enrollment, semester, year, grading_scheme " +
                "FROM sections WHERE semester = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, semester);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String sectionID = String.valueOf(rs.getInt("section_id"));
                int courseID = rs.getInt("course_id");
                String instructorID = String.valueOf(rs.getInt("instructor_id"));
                String room = rs.getString("room");
                int sem = parseSemester(rs.getString("semester"));
                int year = rs.getInt("year");
                int capacity = rs.getInt("capacity");
                int currentEnrollment = rs.getInt("current_enrollment");

                //
                Section section = new Section(
                        courseID,
                        instructorID,
                        sectionID,
                        room,
                        sem,
                        year,
                        capacity,
                        null);

                section.setCurrentEnrollment(currentEnrollment);
                String gradingJson = rs.getString("grading_scheme");
                if (gradingJson != null && !gradingJson.isEmpty()) {
                    Map<String, Integer> gradingMap = parseGradingJson(gradingJson);
                    section.setGradingMap(gradingMap);
                }
                String dayTime = rs.getString("day_time");
                if (dayTime != null && !dayTime.isEmpty()) {
                    section.setTimeTable(parseDayTime(dayTime));
                }

                sections.add(section);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sections;

    }

    public List<Section> getAllEnrolledSections(int studentId) {
        List<Section> list = new ArrayList<>();

        String sqlEnroll = "SELECT section_id FROM enrollments WHERE student_id = ? AND status='enrolled'";
        String sqlSection = "SELECT * FROM sections WHERE section_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement psEnroll = conn.prepareStatement(sqlEnroll)) {

            psEnroll.setInt(1, studentId);
            ResultSet rsEnroll = psEnroll.executeQuery();

            while (rsEnroll.next()) {

                int sectionId = rsEnroll.getInt("section_id");

                try (PreparedStatement psSection = conn.prepareStatement(sqlSection)) {
                    psSection.setInt(1, sectionId);
                    ResultSet rs = psSection.executeQuery();

                    if (rs.next()) {

                        int courseId = rs.getInt("course_id");
                        String instructorId = String.valueOf(rs.getInt("instructor_id"));
                        String room = rs.getString("room");
                        int semester = parseSemester(rs.getString("semester"));
                        int year = rs.getInt("year");
                        int capacity = rs.getInt("capacity");
                        int currentEnrollment = rs.getInt("current_enrollment");

                        Section s = new Section(
                                courseId,
                                instructorId,
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

                        list.add(s);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<Section> getSectionsForStudent(int studentId, int semesterNumber) {
        List<Section> list = new ArrayList<>();

        String sqlEnroll = "SELECT section_id FROM enrollments WHERE student_id = ? AND status='enrolled'";
        String sqlSection = "SELECT * FROM sections WHERE section_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement psEnroll = conn.prepareStatement(sqlEnroll)) {
            psEnroll.setInt(1, studentId);
            ResultSet rsEnroll = psEnroll.executeQuery();

            while (rsEnroll.next()) {

                int sectionId = rsEnroll.getInt("section_id");
                try (PreparedStatement psSection = conn.prepareStatement(sqlSection)) {
                    psSection.setInt(1, sectionId);
                    ResultSet rs = psSection.executeQuery();

                    if (rs.next()) {

                        
                        String semString = rs.getString("semester");
                        int sectionSemester = parseSemester(semString);
                        if (sectionSemester != semesterNumber)
                            continue;

                        int courseID = rs.getInt("course_id");
                        String instructorID = String.valueOf(rs.getInt("instructor_id"));
                        String room = rs.getString("room");
                        int year = rs.getInt("year");
                        int capacity = rs.getInt("capacity");
                        int currentEnrollment = rs.getInt("current_enrollment");

                        Section s = new Section(
                                courseID,
                                instructorID,
                                String.valueOf(sectionId),
                                room,
                                sectionSemester,
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

                        list.add(s);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public int getAvailableSeats(int sectionId) {
        String sql = "SELECT capacity - current_enrollment AS available FROM sections WHERE section_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("available");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Section> getSectionBycouseid(int courseid) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT * FROM sections WHERE course_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseid);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Section section = new Section(
                        rs.getInt("course_id"),
                        String.valueOf(rs.getInt("instructor_id")),
                        String.valueOf(rs.getInt("section_id")),
                        rs.getString("room"),
                        parseSemester(rs.getString("semester")),
                        rs.getInt("year"),
                        rs.getInt("capacity"),
                        null);
                section.setCurrentEnrollment(rs.getInt("current_enrollment"));
                section.setGradingMap(parseGradingJson(rs.getString("grading_scheme")));
                section.setTimeTable(parseDayTime(rs.getString("day_time")));
                sections.add(section);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sections;

    }

    public Section getSectionDetails(int sectionId) {
        Section section = null;
        String sql = "SELECT * FROM sections WHERE section_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                section = new Section(
                        rs.getInt("course_id"),
                        String.valueOf(rs.getInt("instructor_id")),
                        String.valueOf(rs.getInt("section_id")),
                        rs.getString("room"),
                        parseSemester(rs.getString("semester")),
                        rs.getInt("year"),
                        rs.getInt("capacity"),
                        null);
                section.setCurrentEnrollment(rs.getInt("current_enrollment"));
                section.setGradingMap(parseGradingJson(rs.getString("grading_scheme")));
                section.setTimeTable(parseDayTime(rs.getString("day_time")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return section;
    }

    public boolean addSection(Section section) {
        String sql = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year, grading_scheme, current_enrollment) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, section.getCourseID());
            if (section.getInstructorID() == null || section.getInstructorID().trim().isEmpty()) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, Integer.parseInt(section.getInstructorID()));
            }
            ps.setString(3, convertTimeTableToString(section.getTimeTable()));
            ps.setString(4, section.getRoom());
            ps.setInt(5, section.getSectionCapacity());
            ps.setString(6, "Sem" + section.getSemester());
            ps.setInt(7, section.getYear());
            ps.setString(8, convertGradingMapToJson(section.getGradingMap()));

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSection(int sectionId,
            String dayTime,
            String room,
            String semester,
            int year,
            int capacity,
            int enrollment) {

        String sql = "UPDATE sections SET "
                + "day_time = ?, "
                + "room = ?, "
                + "semester = ?, "
                + "year = ?, "
                + "capacity = ?, "
                + "current_enrollment = ? "
                + "WHERE section_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, dayTime);
            ps.setString(2, room);
            ps.setString(3, semester);
            ps.setInt(4, year);
            ps.setInt(5, capacity);
            ps.setInt(6, enrollment);
            ps.setInt(7, sectionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSection(int sectionId) {
        String sql = "DELETE FROM sections WHERE section_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sectionId);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Map<String, Integer> getWeightageForSection(int sectionId) {
        Map<String, Integer> map = new LinkedHashMap<>();

        String sql = "SELECT grading_scheme FROM sections WHERE section_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String json = rs.getString("grading_scheme");
                if (json != null && !json.isEmpty()) {
                    map = parseGradingJson(json);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public boolean updateWeightage(int sectionId, Map<String, Integer> weightage, int bonus) {

        Gson gson = new Gson();
        String json = gson.toJson(weightage); 

        String sql = "UPDATE sections SET grading_scheme = ? WHERE section_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, json);
            ps.setInt(2, sectionId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
