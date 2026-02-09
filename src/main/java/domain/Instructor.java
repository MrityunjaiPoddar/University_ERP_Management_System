package domain;

import java.util.*;

public class Instructor extends User {
    private String name;
    private String department;
    private ArrayList<String> sections;
    private String designation;

    public Instructor(int userID, String userName, String name, String department) {
        super(userID, userName, "Instructor");
        this.name = name;
        this.department = department;
        this.sections = new ArrayList<String>();
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public List<String> getSections() {
        return sections;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSections(ArrayList<String> sections) {
        this.sections = sections;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

}
