package Server_Java.implementation;

import Server_Java.dao.LoginServiceDAO;
import compilations.InvalidCredentialsException;
import compilations.LoginResult;
import compilations.LoginServicePOA;
import compilations.UserAlreadyLoggedInException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the CORBA LoginService.
 * Handles player and admin login, session management, and logout functionality.
 * Maintains active sessions and prevents duplicate logins unless explicitly forced.
 */
public class LoginServiceImpl extends LoginServicePOA {
    // Maps to track user sessions
    private final Map<String, String> userToSessionId = new ConcurrentHashMap<>();
    private final Map<String, String> sessionIdToUser = new ConcurrentHashMap<>();

    /**
     * Handles login request for a player. If the player is already logged in,
     * the old session will be terminated and a new session created.
     *
     * @param username the player's username
     * @param password the player's password
     * @return LoginResult containing session ID and status flags
     * @throws InvalidCredentialsException if credentials are incorrect
     * @throws UserAlreadyLoggedInException if player is already logged in
     */
    @Override
    public LoginResult loginPlayer(String username, String password) throws InvalidCredentialsException, UserAlreadyLoggedInException {
        // Calls forceLoginPlayer with table "players"
        log("Login attempt by player: " + username);
        return forceLoginPlayer(username, password, "players");
    }

    /**
     * Handles login request for an admin. Similar to player login, this forcibly logs out
     * any existing sessions for the admin and creates a new session.
     *
     * @param username the admin's username
     * @param password the admin's password
     * @return LoginResult containing session ID and status flags
     * @throws InvalidCredentialsException if credentials are incorrect
     * @throws UserAlreadyLoggedInException if admin is already logged in
     */
    @Override
    public LoginResult loginAdmin(String username, String password) throws InvalidCredentialsException, UserAlreadyLoggedInException {
        // Calls forceLoginPlayer with table "admins"
        log("Login attempt by admin: " + username);
        return forceLoginPlayer(username, password, "admins");
    }

    /**
     * Validates user credentials and forcibly logs in the user by terminating any existing session.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param table the database table to validate against ("players" or "admins")
     * @return LoginResult with the session ID and indication of forced login
     * @throws InvalidCredentialsException if authentication fails
     */
    @Override
    public LoginResult forceLoginPlayer(String username, String password, String table) throws InvalidCredentialsException {
        log("Validating credentials for " + username + " in table: " + table);

        // Validate credentials using DAO
        LoginServiceDAO.validateAdminCredentials(username, password, table);

        boolean forced = false;

        // If user is already logged in, force logout old session
        if (userToSessionId.containsKey(username)) {
            String oldSessionId = userToSessionId.get(username);
            sessionIdToUser.remove(oldSessionId);
            userToSessionId.remove(username);
            log("User " + username + " was already logged in. Forcing logout of old session.");
            forced = true;
        }

        // Create new session id and map it
        String sessionId = createSession(username);

        if (table.equals("admins")) {
            log("Admin " + username + " successfully logged in. Session ID: " + sessionId);
        } else if (table.equals("players")) {
            log("Player " + username + " successfully logged in. Session ID: " + sessionId);
        }

        return new LoginResult(true, forced, sessionId);
    }

    /**
     * Logs out the user associated with the given session ID.
     * Removes the session-user mappings.
     *
     * @param sessionId the session ID of the user to log out
     */
    @Override
    public void logoutPlayer(String sessionId) {
        // Remove session and user mapping to log out
        if (sessionIdToUser.containsKey(sessionId)) {
            String username = sessionIdToUser.remove(sessionId);
            userToSessionId.remove(username);
            log("User " + username + " successfully logged out.");
        } else {
            log("Invalidated session ID " + sessionId);
        }
    }

    /**
     * Checks if a session is currently active.
     *
     * @param sessionId the session ID to check
     * @return true if the session is active; false otherwise
     */
    @Override
    public boolean isSessionActive(String sessionId) {
        return sessionIdToUser.containsKey(sessionId);
    }

    /**
     * Generates a new session ID and maps it to the given username.
     *
     * @param username the username to associate with the session
     * @return the newly generated session ID
     */
    private String createSession(String username) {
        String sessionId = UUID.randomUUID().toString();
        userToSessionId.put(username, sessionId);
        sessionIdToUser.put(sessionId, username);
        log("Session created for user " + username + ": " + sessionId);
        return sessionId;
    }

    /**
     * Utility method for logging server-side actions with a timestamp.
     *
     * @param message the message to log
     */
    public static void log(String message) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + timestamp + "] [SERVER] " + message);
    }
}