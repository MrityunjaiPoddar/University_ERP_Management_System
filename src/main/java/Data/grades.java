package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import auth.authsession;
import domain.Grade;
import domain.gradeslab;

public class grades {
    public Grade getGradesByEnrollment(int enrollmentId) {
        String sql = "SELECT component, score, final_grade FROM grades WHERE enrollment_id = ?";
        Grade grade = new Grade(enrollmentId, "", 0.0);
        Map<String, Double> marks = new HashMap<>();
        String finalGrade = null;
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                marks.put(rs.getString("component"), rs.getDouble("score"));
                finalGrade = rs.getString("final_grade");
            }

            grade.setMarks(marks);
            grade.setFinalGrade(finalGrade);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return grade;
    }

    public int gettotalmarks(int enrollmentId) {
        int total = 0;
        String sql = "SELECT SUM(score) AS total_score FROM grades WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total_score");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public String getFinalGrade(int enrollmentId) {
        String finalGrade = null;
        String sql = "SELECT final_grade FROM grades WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                finalGrade = rs.getString("final_grade");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return finalGrade;
    }

    public double getComponentScore(int enrollmentId, String component) {
        double score = -1;
        String sql = "SELECT score FROM grades WHERE enrollment_id = ? AND component = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setString(2, component);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                score = rs.getDouble("score");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return score;
    }

    public boolean componentExists(int enrollmentId, String component) {
        String sql = "SELECT 1 FROM grades WHERE enrollment_id = ? AND component = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setString(2, component);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertComponent(int enrollmentId, String component, double score, int weight) {

        if (componentExists(enrollmentId, component)) {
            return updateScore(enrollmentId, component, score);
        }

        String sql = "INSERT INTO grades (enrollment_id, component, score, weight) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ps.setString(2, component);
            ps.setDouble(3, score);
            ps.setInt(4, weight);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateScore(int enrollmentId, String component, double newScore) {
        String sql = "UPDATE grades SET score = ? WHERE enrollment_id = ? AND component = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newScore);
            ps.setInt(2, enrollmentId);
            ps.setString(3, component);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double computeWeightedTotal(int enrollmentId) {
        double total = 0;
        String sql = "SELECT score, weight FROM grades WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                double score = rs.getDouble("score");
                total += score;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public boolean setFinalGrade(int enrollmentId, String gradeLetter) {
        String sql = "UPDATE grades SET final_grade = ? WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, gradeLetter);
            ps.setInt(2, enrollmentId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String computeFinalLetterGrade(int enrollmentId, int sectionId) {

        
        double total = computeWeightedTotal(enrollmentId);

        gradeslabdatalayer slabDL = new gradeslabdatalayer();
        int instructorId = new Instructordatalayer().getInstructorIdFromUsername(authsession.getUsername());

        List<gradeslab> slabs = slabDL.getSlabsForSection(instructorId, sectionId);

        if (slabs == null || slabs.isEmpty()) {
            System.err.println("No grade slabs defined for section " + sectionId);
            return null;
        }

        for (gradeslab slab : slabs) {
            if (total >= slab.getMinMarks() && total <= slab.getMaxMarks()) {
                return slab.getGrade();
            }
        }

        return "F";
    }

    public boolean allComponentsAssigned(int enrollmentId, int sectionId) {

        sectiondatalayer secDL = new sectiondatalayer();
        Map<String, Integer> weightMap = secDL.getWeightageForSection(sectionId);

        int requiredComponents = weightMap.size();

        String sql = "SELECT COUNT(*) AS cnt FROM grades WHERE enrollment_id = ?";
        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, enrollmentId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int stored = rs.getInt("cnt");
                return stored == requiredComponents;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Grade> getGradesForSection(int sectionId) {
        List<Grade> gradeList = new ArrayList<>();

        try (Connection conn = DBConnection.getMainConnection()) {

            List<Integer> enrollIds = new ArrayList<>();
            String sql1 = "SELECT enrollment_id FROM enrollments WHERE section_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, sectionId);
            ResultSet rs1 = ps1.executeQuery();

            while (rs1.next()) {
                enrollIds.add(rs1.getInt("enrollment_id"));
            }

            if (enrollIds.isEmpty())
                return gradeList;
            String sql2 = "SELECT component, score, final_grade FROM grades WHERE enrollment_id = ?";

            for (int enrollId : enrollIds) {
                PreparedStatement ps2 = conn.prepareStatement(sql2);
                ps2.setInt(1, enrollId);
                ResultSet rs2 = ps2.executeQuery();

                Grade g = new Grade(enrollId, "", 0.0);
                Map<String, Double> marks = new HashMap<>();
                String finalGrade = null;
                while (rs2.next()) {
                    marks.put(rs2.getString("component"), rs2.getDouble("score"));
                    finalGrade = rs2.getString("final_grade");
                }
                g.setMarks(marks);
                g.setFinalGrade(finalGrade);
                gradeList.add(g);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return gradeList;
    }

    public Map<String, Double> getGradesAverageForSectionandComponentwise(int sectionId) {
        Map<String, Double> averageMap = new HashMap<>();
        List<Grade> gradesList = getGradesForSection(sectionId);
        for (Grade g : gradesList) {
            Map<String, Double> marks = g.getMarks();
            for (String component : marks.keySet()) {
                averageMap.putIfAbsent(component, 0.0);
                averageMap.put(component, averageMap.get(component) + marks.get(component));
            }
        }
        int numStudents = gradesList.size();
        for (String component : averageMap.keySet()) {
            averageMap.put(component, averageMap.get(component) / numStudents);
        }

        return averageMap;
    }
}
