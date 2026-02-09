package domain;

public class gradeslab {
    private int slabId; // optional (only used when reading from DB)
    private int instructorId;
    private int sectionId;

    private String grade;
    private int minMarks;
    private int maxMarks;
    private double gradePoint;

    public gradeslab(String grade, int minMarks, int maxMarks, double gradePoint) {
        this.grade = grade;
        this.minMarks = minMarks;
        this.maxMarks = maxMarks;
        this.gradePoint = gradePoint;
    }

    public gradeslab(int slabId, int instructorId, int sectionId,
            String grade, int minMarks, int maxMarks, double gradePoint) {

        this.slabId = slabId;
        this.instructorId = instructorId;
        this.sectionId = sectionId;
        this.grade = grade;
        this.minMarks = minMarks;
        this.maxMarks = maxMarks;
        this.gradePoint = gradePoint;
    }

    public int getSlabId() {
        return slabId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public String getGrade() {
        return grade;
    }

    public int getMinMarks() {
        return minMarks;
    }

    public int getMaxMarks() {
        return maxMarks;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public void setSlabId(int slabId) {
        this.slabId = slabId;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setMinMarks(int minMarks) {
        this.minMarks = minMarks;
    }

    public void setMaxMarks(int maxMarks) {
        this.maxMarks = maxMarks;
    }

    public void setGradePoint(double gradePoint) {
        this.gradePoint = gradePoint;
    }
}
