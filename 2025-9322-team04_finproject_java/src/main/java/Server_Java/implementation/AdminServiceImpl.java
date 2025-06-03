package Server_Java.implementation;

import Server_Java.dao.AdminServiceDAO;
import compilations.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementation of the AdminService CORBA interface.
 * Provides administrative functionality such as player account management,
 * game configuration updates, and game session history retrieval.
 */
public class AdminServiceImpl extends AdminServicePOA {

    /**
     * Creates a new player account with the specified username and password.
     *
     * @param username the desired username
     * @param password the desired password
     * @throws PlayerAlreadyExistsException if the username is already taken
     */
    @Override
    public void createPlayerAccount(String username, String password) throws PlayerAlreadyExistsException {
        log("Request to create player account: " + username + " [" + password + "]");
        try {
            boolean success = AdminServiceDAO.createPlayer(username, password);
            if (!success) {
                log("Failed to create player account: " + username + " (Already exists!)");
                throw new PlayerAlreadyExistsException();
            }
            log("Successfully created player account: " + username);
        } catch (Exception e) {
            log("Exception while creating account for " + username + ": " + e.getMessage()) ;
            throw new PlayerAlreadyExistsException();
        }
    }

    /**
     * Removes an existing player account.
     *
     * @param username the username of the player to remove
     * @throws PlayerNotFoundException if the player does not exist
     */
    @Override
    public void removePlayerAccount(String username) throws PlayerNotFoundException {
        log("Request to remove player account: " + username);
        try {
            boolean success = AdminServiceDAO.removePlayer(username);
            if (!success) {
                log("Player not found for removal: " + username);
                throw new PlayerNotFoundException();
            }
            log("Successfully removed player account: " + username);
        } catch (Exception e) {
            log("Exception while removing account " + username + ": " + e.getMessage());
            throw new PlayerNotFoundException();
        }
    }

    /**
     * Updates a player's credentials (username and/or password).
     *
     * @param currentUsername the existing username
     * @param newUsername     the new username
     * @param newPassword     the new password
     * @throws PlayerNotFoundException     if the current username does not exist
     * @throws PlayerAlreadyExistsException if the new username is already in use
     */
    @Override
    public void updatePlayerCredentials(String currentUsername, String newUsername, String newPassword)
            throws PlayerNotFoundException, PlayerAlreadyExistsException {

        log("Request to update credentials of user '" + currentUsername + "' to [" + newUsername + ", " + newPassword + "]");
        try {
            boolean success = AdminServiceDAO.updatePlayerCredentials(currentUsername, newUsername, newPassword);
            if (!success) {
                log("Player " + currentUsername + " not found during credential update.");
                throw new PlayerNotFoundException("Player with username '" + currentUsername + "' not found.");
            }
            log("Successfully updated credentials for player " + currentUsername);
        } catch (SQLException e) {
            if (e.getMessage().contains("duplicate")) {
                log("Username already exists during credential update: " + newUsername);
                throw new PlayerAlreadyExistsException("Username '" + newUsername + "' already exists.");
            }
            log("SQL error during credential update: " + e.getMessage());
            throw new PlayerNotFoundException("Error updating player credentials: " + e.getMessage());
        }
    }

    /**
     * Searches for player accounts based on a keyword.
     *
     * @param keyword the keyword to search for
     * @return an array of matching player accounts
     */
    @Override
    public PlayerAccount[] searchPlayerAccounts(String keyword) {
        log("Searching player accounts with keyword: " + keyword);
        try {
            List<PlayerAccount> players = AdminServiceDAO.searchPlayerAccounts(keyword);
            log("Found " + players.size() + " player accounts matching keyword: " + keyword);
            return players.toArray(new PlayerAccount[0]);
        } catch (Exception e) {
            log("Error searching player accounts: " + e.getMessage());
            return new PlayerAccount[0]; // Return empty array on error
        }
    }

    /**
     * Updates the game wait time before the game starts.
     *
     * @param seconds the new wait time in seconds
     */
    @Override
    public void updateGameWaitTime(short seconds) {
        log("Updating game wait time to " + seconds + " seconds");
        // Implement wait time update logic here
        AdminServiceDAO.updateGameWaitTime(seconds);
    }

    /**
     * Updates the duration of each game round.
     *
     * @param seconds the new round duration in seconds
     */
    @Override
    public void updateRoundDuration(short seconds) {
        log("Updating round duration to " + seconds + " seconds");

        AdminServiceDAO.updateRoundDuration(seconds);
    }

    /**
     * Retrieves the current wait time setting for joining a game.
     *
     * @return the join timeout in seconds
     */
    @Override
    public short getJoinTimeout() {
        short waitTime = AdminServiceDAO.getWaitingTime();
        return waitTime;
    }

    /**
     * Retrieves the current round duration setting.
     *
     * @return the round duration in seconds
     */
    @Override
    public short getRoundDuration() {
        short duration = AdminServiceDAO.getRoundDuration();
        return duration;
    }

    /**
     * Retrieves the history of completed game sessions.
     *
     * @return an array of past game session objects
     */
    @Override
    public GameSessionObject[] getGameHistory() {
        log("Retrieving game history from DAO");
        GameSessionObject[] sessions = AdminServiceDAO.getGameHistory();
        int count = (sessions != null) ? sessions.length : 0;
        log("Retrieved " + count + " game sessions");

        if (sessions != null) {
            for (int i = 0; i < sessions.length; i++) {
                GameSessionObject gs = sessions[i];
                log("Session " + i+1 + ": ID=" + gs.gameId + ", Time=" + gs.timeCreated + ", Status=" + gs.status);
            }
        } else {
            log("Game history returned null");
        }

        return sessions != null ? sessions : new GameSessionObject[0];
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