package domain;

import java.util.*;
import java.time.*;

public class Section {
    private int courseID;
    private String instructorID;
    private String sectionID;
    private int sectionCapacity;
    private int currentEnrollment;
    private Map<String, Integer> gradingMap;
    private LocalDate deadline;
    private Map<DayOfWeek, TimeSlot> timeTable;

    private String room;
    private int semester;
    private int year;

    public Section(int courseID, String instructorID, String sectionID, String room, int semester, int year,
            int sectionCapacity, LocalDate deadline) {
        this.courseID = courseID;
        this.instructorID = instructorID;
        this.sectionID = sectionID;
        this.room = room;
        this.semester = semester;
        this.year = year;
        this.sectionCapacity = sectionCapacity;
        this.timeTable = new HashMap<DayOfWeek, TimeSlot>();

        this.currentEnrollment = 0;
        this.gradingMap = new HashMap<String, Integer>();
        this.deadline = deadline;
    }

    public int getSectionCapacity() {
        return sectionCapacity;
    }

    public int getCurrentEnrollment() {
        return currentEnrollment;
    }

    public Map<String, Integer> getGradingMap() {
        return gradingMap;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Map<DayOfWeek, TimeSlot> getTimeTable() {
        return timeTable;
    }

    public void setSectionCapacity(int sectionCapacity) {
        this.sectionCapacity = sectionCapacity;
    }

    public void setCurrentEnrollment(int currentEnrollment) {
        this.currentEnrollment = currentEnrollment;
    }

    public void setGradingMap(Map<String, Integer> gradingMap) {
        this.gradingMap = gradingMap;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public void setTimeTable(Map<DayOfWeek, TimeSlot> timeTable) {
        this.timeTable = timeTable;
    }

    public int getAvailableSeats() {
        return this.sectionCapacity - this.currentEnrollment;
    }

    public void incrementEnrollment() {
        if (this.currentEnrollment < this.sectionCapacity) {
            ++this.currentEnrollment;
        }
    }

    public void decrementEnrollment() {
        if (this.currentEnrollment > 0) {
            --this.currentEnrollment;
        }
    }

    public int getCourseID() {
        return courseID;
    }

    public String getInstructorID() {
        return instructorID;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public void setInstructorID(String instructorID) {
        this.instructorID = instructorID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public String getRoom() {
        return room;
    }

    public int getSemester() {
        return semester;
    }

    public int getYear() {
        return year;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public void setYear(int year) {
        this.year = year;
    }

}
