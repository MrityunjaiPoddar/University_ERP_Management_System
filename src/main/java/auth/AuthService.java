package auth;

import javax.swing.*;

public class AuthService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION = 5 * 60 * 1000;

    public static boolean login(String username, String password) {

        long now = System.currentTimeMillis();

        long lockoutUntil = UserStore.getLockoutUntil(username);

        if (lockoutUntil > now) {
            long secondsLeft = (lockoutUntil - now) / 1000;
            JOptionPane.showMessageDialog(null,
                    "Too many failed attempts.\nTry again after " + secondsLeft + " seconds.",
                    "Account Locked",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (lockoutUntil != 0 && lockoutUntil <= now) {
            UserStore.resetLockout(username);
        }

        boolean isValid = UserStore.verifyUser(username, password);

        if (!isValid) {
            int attempts = UserStore.getFailedAttempts(username) + 1;

            UserStore.updateFailedAttempts(username, attempts);

            if (attempts >= MAX_ATTEMPTS) {
                long lockUntil = now + LOCKOUT_DURATION;
                UserStore.lockUser(username, lockUntil);

                JOptionPane.showMessageDialog(null,
                        "Too many incorrect attempts.\nAccount locked for 5 minutes.",
                        "LOCKED",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            JOptionPane.showMessageDialog(null,
                    "Incorrect password.\nAttempts left: " + (MAX_ATTEMPTS - attempts),
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        UserStore.resetLockout(username);

        String role = UserStore.getUserRole(username);
        authsession.set(username, role);

        return true;
    }
}