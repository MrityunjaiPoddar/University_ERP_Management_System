package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Stats {
    public Map<String, List<Double>> getComponentScores(int sectionId) {
        Map<String, List<Double>> map = new HashMap<>();

        String sql = """
                SELECT g.component, g.score
                FROM grades g
                JOIN enrollments e ON g.enrollment_id = e.enrollment_id
                WHERE e.section_id = ?
                """;

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String comp = rs.getString("component");
                double score = rs.getDouble("score");

                map.computeIfAbsent(comp, k -> new ArrayList<>()).add(score);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }

    public Map<String, Integer> getGradeDistribution(int sectionId) {

        Map<String, Integer> dist = new LinkedHashMap<>();

        String sql = """
                SELECT final_grade, COUNT(DISTINCT enrollment_id) AS cnt
                FROM grades
                WHERE enrollment_id IN (
                    SELECT enrollment_id FROM enrollments WHERE section_id = ?
                )
                AND final_grade IS NOT NULL
                GROUP BY final_grade
                """;

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                dist.put(rs.getString("final_grade"), rs.getInt("cnt"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dist;
    }

    public List<Double> getTotalScores(int sectionId) {
        List<Double> totals = new ArrayList<>();

        String sql = """
                SELECT e.enrollment_id
                FROM enrollments e
                WHERE e.section_id = ?
                """;

        grades gradeDL = new grades();

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int eid = rs.getInt("enrollment_id");
                double total = gradeDL.computeWeightedTotal(eid);
                totals.add(total);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totals;
    }

    public double average(List<Double> list) {
        if (list.isEmpty())
            return 0;
        return list.stream().mapToDouble(a -> a).average().orElse(0);
    }

    public double median(List<Double> list) {
        if (list.isEmpty())
            return 0;
        List<Double> sorted = new ArrayList<>(list);
        Collections.sort(sorted);

        int mid = sorted.size() / 2;

        if (sorted.size() % 2 == 0)
            return (sorted.get(mid - 1) + sorted.get(mid)) / 2.0;
        else
            return sorted.get(mid);
    }

    public double mode(List<Double> list) {
        if (list.isEmpty())
            return 0;

        Map<Double, Integer> freq = new HashMap<>();
        for (double x : list)
            freq.put(x, freq.getOrDefault(x, 0) + 1);

        return Collections.max(freq.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public double stddev(List<Double> list) {
        if (list.isEmpty())
            return 0;

        double avg = average(list);
        double sum = 0;

        for (double x : list)
            sum += Math.pow(x - avg, 2);

        return Math.sqrt(sum / list.size());
    }

}
