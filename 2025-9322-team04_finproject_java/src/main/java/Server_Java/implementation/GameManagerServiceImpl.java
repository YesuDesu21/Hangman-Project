package Server_Java.implementation;

import Server_Java.dao.GameManagerServiceDAO;
import compilations.GameManagerServicePOA;
import compilations.GameNotFoundException;
import compilations.GameSession;
import compilations.GameStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Implementation of the GameManagerService CORBA interface.
 *
 * This class manages game lifecycle operations such as
 * creating, registering, and updating game sessions in the database,
 * and coordinating matchmaking and game state persistence.
 */
public class GameManagerServiceImpl extends GameManagerServicePOA {

    /**
     * Allows a player to join an existing game or creates a new game session if enough players are ready.
     * If the player is the first or not enough players have joined, returns "WAITING".
     *
     * @param username the username of the player joining or creating the session
     * @return the game ID if a new session was started, otherwise "WAITING"
     */
    @Override
    public String joinOrCreateGameSession(String username) {
        // Step 1: Mark the player as ready
        GameManagerServiceDAO.markPlayerReady(username);
        log("Marked player as ready: " + username);

        // Step 2: Try to start a new game session (returns gameId if successful)
        String gameId = GameManagerServiceDAO.checkAndStartGameIfReady();

        if (gameId != null) {
            log("New game session started: " + gameId);
            return gameId;
        }

        // Step 3: Not enough players yet — return "WAITING"
        log("Not enough players to start a game.");
        return "WAITING";
    }

    /**
     * Lists all currently active game sessions with their associated player usernames.
     *
     * @return an array of active GameSession objects
     */
    @Override
    public GameSession[] listActiveGameSessions() {
        Set<String> activeIds = GameManagerServiceDAO.getAllActiveGameIds();
        log("Retrieved " + activeIds.size() + " active game session(s).");

        GameSession[] sessions = new GameSession[activeIds.size()];
        int index = 0;

        for (String gameId : activeIds) {
            Set<String> players = GameManagerServiceDAO.getPlayersInGame(gameId);
            log("[DATABASE] Game " + gameId + " has " + players.size() + " player(s).");

            GameSession session = new GameSession();
            session.gameId = gameId;
            session.players = players.toArray(new String[0]);
            sessions[index++] = session;

        }

        return sessions;
    }

    /**
     * Retrieves the current status of all waiting (not yet started or ongoing) game sessions.
     *
     * @return an array of GameStatus objects representing waiting game sessions
     * @throws GameNotFoundException if no such game sessions are found
     */
    @Override
    public GameStatus[] getGameStatus() throws GameNotFoundException {
        log("[DATABASE] Fetching status of waiting game sessions.");
        return GameManagerServiceDAO.checkWaitingGameStatus();
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