package Server_Java.session;

import Server_Java.dao.GameManagerServiceDAO;
import compilations.GameNotFoundException;
import compilations.NoOpponentFoundException;
import compilations.WordService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Singleton class responsible for managing multiplayer game sessions.
 * Handles matchmaking, word assignment, game lifecycle, player registration,
 * and round progression. Coordinates with the WordService and GameManagerServiceDAO.
 */
public class GameManager {
    private static final GameManager instance = new GameManager();

    private final Map<String, GameSession> activeSessions = new HashMap<>();
    private final Set<String> waitingPlayers = new HashSet<>();
    private final Map<String, GameSession> sessionsByGameId = new HashMap<>();

    private boolean gameStarted = false;

    private WordService wordService;
    private GameSession currentSession;
    private Timer startTimer;

    /**
     * Returns the singleton instance of the GameManager.
     *
     * @return the singleton GameManager instance
     */
    public static GameManager getInstance() {
        return instance;
    }

    /**
     * Sets the WordService dependency used to fetch new words for games.
     *
     * @param service the WordService instance
     */
    public void setWordService(WordService service) {
        this.wordService = service;
    }

    /**
     * Allows a player to join a game. If enough players are available,
     * starts the game after a countdown. Initializes a new session if needed.
     *
     * @param username the player attempting to join
     * @throws NoOpponentFoundException if the game has already started and no opponents can join
     */
    public synchronized void joinGame(String username) throws NoOpponentFoundException {
        if (gameStarted) throw new NoOpponentFoundException();

        waitingPlayers.add(username);

        if (currentSession == null) {
            String gameId = GameManagerServiceDAO.generateSequentialGameId();
            String word = wordService.getNewWord(gameId);

            if (word == null || word.equals("NO_WORD_AVAILABLE")) {
                throw new IllegalStateException("No word available to start a new game.");
            }

            // Register the new game in the DB with status 'waiting'
            try {
                GameManagerServiceDAO.registerNewGame(gameId);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to register new game in the database.", e);
            }

            currentSession = new GameSession(gameId, word);
            sessionsByGameId.put(gameId, currentSession);
            startCountdown();
        }

        currentSession.registerPlayer(username);
        activeSessions.put(username, currentSession);
    }

    /**
     * Returns the current active game session, if any.
     *
     * @return the current GameSession
     */
    public synchronized GameSession getCurrentSession() {
        return currentSession;
    }

    /**
     * Starts a countdown to begin the game. If not enough players have joined,
     * the game is canceled and the session is reset.
     */
    private void startCountdown() {
        gameStarted = false;
        if (startTimer != null) {
            startTimer.cancel();
        }

        startTimer = new Timer();
        startTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (GameManager.this) {
                    if (waitingPlayers.size() < 2) {
                        log("Not enough players. Game will not start.");
                        gameStarted = false;

                        try {
                            if (currentSession != null) {
                                GameManagerServiceDAO.updateGameStatus(currentSession.getGameId(), "cancelled");
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        reset();

                        return;
                    }

                    gameStarted = true;
                    log("Game started with players: " + waitingPlayers);

                    try {
                        if (currentSession != null) {
                            GameManagerServiceDAO.updateGameStatus(currentSession.getGameId(), "ongoing");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace(); // Or use proper logging
                    }

                    waitingPlayers.clear(); // Once the game starts, players are no longer waiting
                }
            }
        }, 10000); // 10 seconds delay
    }

    /**
     * Retrieves the GameSession associated with the given player.
     *
     * @param username the username of the player
     * @return the GameSession for the player
     * @throws GameNotFoundException if the player is not in any active session
     */
    public synchronized GameSession getSession(String username) throws GameNotFoundException {
        GameSession session = activeSessions.get(username);
        if (session == null) throw new GameNotFoundException();
        return session;
    }

    /**
     * Returns the game status for a player.
     *
     * @param username the player username
     * @return "STARTED" if the game is in progress for the player, otherwise "WAITING"
     */
    public synchronized String getStatus(String username) {
        return gameStarted && activeSessions.containsKey(username) ? "STARTED" : "WAITING";
    }

    /**
     * Returns the number of players currently joined in sessions.
     *
     * @return the number of joined players
     */
    public int getJoinedPlayerCount() {
        return activeSessions.size();
    }

    /**
     * Returns a list of all players currently in active sessions.
     *
     * @return list of usernames
     */
    public synchronized List<String> getAllPlayers() {
        return new ArrayList<>(activeSessions.keySet());
    }

    /**
     * Returns the game session associated with a specific game ID.
     *
     * @param gameId the game ID to look up
     * @return the GameSession if found, or null
     */
    public synchronized GameSession getSessionByGameId(String gameId) {
        return sessionsByGameId.get(gameId);
    }

    private boolean gameEnded = false;

    /**
     * Marks the current game as ended.
     */
    public synchronized void markGameEnded() {
        this.gameEnded = true;
    }

    /**
     * Checks if the current game has ended.
     *
     * @return true if the game has ended, false otherwise
     */
    public synchronized boolean hasGameEnded() {
        return gameEnded;
    }

    /**
     * Schedules a reset of the GameManager state after a delay (20 seconds).
     * Used for cleanup after the game ends.
     */
    public synchronized void delayedReset() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (GameManager.this) {
                    currentSession = null;
                    activeSessions.clear();
                    sessionsByGameId.clear();
                    waitingPlayers.clear();
                    gameStarted = false;
                    gameEnded = false;
                }
            }
        }, 20000); // wait 20 seconds before reset
    }

    /**
     * Immediately resets the GameManager, clearing sessions, players, and state.
     * Should be called when game initialization fails or game is canceled.
     */
    public synchronized void reset() {
        this.currentSession = null;
        this.activeSessions.clear();
        this.waitingPlayers.clear();
        this.sessionsByGameId.clear();
        this.gameStarted = false;
        this.startTimer = null;
        this.gameEnded = false;
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