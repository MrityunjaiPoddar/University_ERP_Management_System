package auth;

public class authsession {

    private static String username;
    private static String role;

    public static void set(String user, String userRole) {
        username = user;
        role = userRole;
    }

    public static void set(String user) {
        username = user;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }

    public static void clear() {
        username = null;
        role = null;
    }
}
