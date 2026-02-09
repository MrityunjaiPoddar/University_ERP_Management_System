package domain;

import java.util.*;

public class Student extends User {
    private String name;
    private int rollNumber;
    private String degree;
    private String program;
    private int Semester;
    private double CGPA;
    private ArrayList<String> regSections;
    private int year;

    public Student(int userID, String userName, String name, int rollNumber, String program, int year) {
        super(userID, userName, "Student");
        this.name = name;
        this.rollNumber = rollNumber;
        this.program = program;
        this.year = year;
        this.regSections = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public int getRollNumber() {
        return rollNumber;
    }

    public String getDegree() {
        return degree;
    }

    public String getDepartment() {
        return program;
    }

    public int getSemester() {
        return Semester;
    }

    public double getCGPA() {
        return CGPA;
    }

    public List<String> getRegSections() {
        return regSections;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRollNumber(int rollNumber) {
        this.rollNumber = rollNumber;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public void setDepartment(String program) {
        this.program = program;
    }

    public void setSemester(int semester) {
        Semester = semester;
    }

    public void setCGPA(double cGPA) {
        CGPA = cGPA;
    }

    public void setRegSections(ArrayList<String> regSections) {
        this.regSections = regSections;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

}