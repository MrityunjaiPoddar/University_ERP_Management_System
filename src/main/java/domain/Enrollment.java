package domain;

public class Enrollment {
    private int enrollmentID;
    private int studentRollNumber;
    private String sectionID;
    private EnrollmentStatus status;

    public Enrollment(int studentRollNumber, String sectionID) {
        this.studentRollNumber = studentRollNumber;
        this.sectionID = sectionID;
        this.status = EnrollmentStatus.ENROLLED;
    }

    public int getStudentRollNumber() {
        return studentRollNumber;
    }

    public String getSectionID() {
        return sectionID;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) {
        this.status = status;
    }

    public int getEnrollmentID() {
        return enrollmentID;
    }

    public void setEnrollmentID(int enrollmentID) {
        this.enrollmentID = enrollmentID;
    }

}
