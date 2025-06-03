package Client_Java.admin.model;

import Client_Java.admin.AdminCorbaManager;
import compilations.InvalidCredentialsException;
import compilations.LoginResult;
import compilations.UserAlreadyLoggedInException;

public class AdminLoginModel {
    private String username;
    private String password;

    private final AdminCorbaManager corbaManager;

    public AdminLoginModel() {
        this.corbaManager = AdminCorbaManager.getInstance();
    };

    public boolean verifyAdminCredentials(String username, String password) {
        try {
            LoginResult result = corbaManager.getLoginService().loginAdmin(username, password);
            boolean success = result.success;

            if (success) {
                corbaManager.setCurrentAdminUsername(username);
                corbaManager.setSessionId(result.sessionId); // <-- Store session ID
                this.username = username;
                this.password = password;
                System.out.println("Admin logged in: " + username);
                return true;
            } else {
                System.out.println("Login failed for: " + username);
                return false;
            }
        } catch (InvalidCredentialsException ice) {
            System.err.println("Error during login verification.");
            return false;
        } catch (UserAlreadyLoggedInException e) {
            System.err.println("User is already logged in.");
            return false;
        }
    }

    public boolean isSessionValid() {
        try {
            String sessionId = corbaManager.getSessionId();
            return corbaManager.getLoginService().isSessionActive(sessionId);
        } catch (Exception e) {
            System.err.println("Failed to check session validity.");
            return false;
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}