package domain;

import java.util.*;

public class Grade {
    private int enrollmentID;
    private Map<String, Double> Marks;
    private String finalGrade;

    public Grade(int enrollmentId, String component, double score) {
        this.enrollmentID = enrollmentId;
        this.Marks = new HashMap<String, Double>();
    }

    public int getEnrollmentID() {
        return enrollmentID;
    }

    public String getFinalGrade() {
        return finalGrade;
    }

    public void setEnrollmentID(int enrollmentId) {
        this.enrollmentID = enrollmentId;
    }

    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
    }

    public Map<String, Double> getMarks() {
        return Marks;
    }

    public void setMarks(Map<String, Double> marks) {
        Marks = marks;
    }

}
