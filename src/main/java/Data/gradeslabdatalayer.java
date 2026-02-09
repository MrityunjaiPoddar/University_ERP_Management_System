package Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import domain.gradeslab;

public class gradeslabdatalayer {
    public boolean saveSlabsForSection(int instructorId, int sectionId, List<gradeslab> slabs) {

        String deleteSQL = "DELETE FROM grade_slabs WHERE instructor_id = ? AND section_id = ?";
        String insertSQL = "INSERT INTO grade_slabs (instructor_id, section_id, grade, min_marks, max_marks, grade_point) "
                +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement delStmt = null;
        PreparedStatement insStmt = null;

        try {
            conn = DBConnection.getMainConnection();
            delStmt = conn.prepareStatement(deleteSQL);
            delStmt.setInt(1, instructorId);
            delStmt.setInt(2, sectionId);
            delStmt.executeUpdate();

            insStmt = conn.prepareStatement(insertSQL);

            for (gradeslab slab : slabs) {

                insStmt.setInt(1, instructorId);
                insStmt.setInt(2, sectionId);
                insStmt.setString(3, slab.getGrade());
                insStmt.setInt(4, slab.getMinMarks());
                insStmt.setInt(5, slab.getMaxMarks());
                insStmt.setDouble(6, slab.getGradePoint());
                insStmt.addBatch();
            }

            insStmt.executeBatch();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null)
                    conn.rollback();
            } catch (Exception ignored) {
            }
            return false;

        } finally {
            try {
                if (delStmt != null)
                    delStmt.close();
            } catch (Exception ignored) {
            }
            try {
                if (insStmt != null)
                    insStmt.close();
            } catch (Exception ignored) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception ignored) {
            }
        }
    }

    public List<gradeslab> getSlabsForSection(int instructorId, int sectionId) {

        List<gradeslab> list = new ArrayList<>();

        String sql = "SELECT grade, min_marks, max_marks, grade_point FROM grade_slabs " +
                "WHERE instructor_id = ? AND section_id = ? ORDER BY grade_point DESC";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, instructorId);
            ps.setInt(2, sectionId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                gradeslab slab = new gradeslab(
                        rs.getString("grade"),
                        rs.getInt("min_marks"),
                        rs.getInt("max_marks"),
                        rs.getDouble("grade_point"));
                list.add(slab);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean deleteSlab(int slabId) {
        String sql = "DELETE FROM grade_slabs WHERE slab_id = ?";

        try (Connection conn = DBConnection.getMainConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, slabId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
