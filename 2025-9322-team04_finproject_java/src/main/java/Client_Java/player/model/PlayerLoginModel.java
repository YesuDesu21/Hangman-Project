package Client_Java.player.model;

import Client_Java.player.PlayerCorbaManager;
import compilations.InvalidCredentialsException;
import compilations.LoginResult;
import compilations.UserAlreadyLoggedInException;

public class PlayerLoginModel {
    private String username;
    private String password;

    private final PlayerCorbaManager corbaManager;

    public PlayerLoginModel() {
        this.corbaManager = PlayerCorbaManager.getInstance();
    }

    public boolean verifyPlayerCredentials(String username, String password) {
        try {
            LoginResult result = corbaManager.getLoginService().loginPlayer(username, password);
            boolean success = result.success;

            if (success) {
                corbaManager.setCurrentPlayerUsername(username);
                corbaManager.setSessionId(result.sessionId); // <-- Store session ID
                this.username = username;
                this.password = password;
                System.out.println("Player logged in: " + username);
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
        String sessionId = PlayerCorbaManager.getInstance().getSessionId();
        try {
            return PlayerCorbaManager.getInstance()
                    .getLoginService()
                    .isSessionActive(sessionId);
        } catch (Exception e) {
            System.err.println("Session validation failed: " + e.getMessage());
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