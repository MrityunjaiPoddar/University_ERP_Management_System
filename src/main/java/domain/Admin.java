package domain;

public class Admin extends User {
    private String name;

    public Admin(int userID, String userName, String name) {
        super(userID, userName, "Admin");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
