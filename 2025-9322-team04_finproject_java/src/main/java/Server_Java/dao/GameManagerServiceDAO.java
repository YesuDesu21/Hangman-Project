package Server_Java.dao;

import Server_Java.manager.DatabaseManager;
import compilations.GameStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GameManagerServiceDAO {
    // Players who clicked "Start Game" and are waiting
    private static final Map<String, Long> waitingPlayers = new ConcurrentHashMap<>();

    // Maps game ID to players in that game
    private static final Map<String, Set<String>> gameSessions = new ConcurrentHashMap<>();

    // Maps player username to their current game ID
    private static final Map<String, String> playerToGame = new ConcurrentHashMap<>();

    // Time limit to wait for others (in ms)
    private static final long WAIT_TIME_MS = 10_000;

    public static synchronized void markPlayerReady(String username) {
        waitingPlayers.put(username, System.currentTimeMillis());
    }

    public static synchronized String checkAndStartGameIfReady() {
        long now = System.currentTimeMillis();

        // Filter players who are still within wait time
        List<String> readyPlayers = new ArrayList<>();
        for (Map.Entry<String, Long> entry : waitingPlayers.entrySet()) {
            if (now - entry.getValue() <= WAIT_TIME_MS) {
                readyPlayers.add(entry.getKey());
            }
        }

        if (readyPlayers.size() >= 2) {
            // Create new game session
            String gameId = generateSequentialGameId();
            Set<String> group = new HashSet<>(readyPlayers);

            // Assign players to session
            for (String player : group) {
                playerToGame.put(player, gameId);
                waitingPlayers.remove(player);
            }

            gameSessions.put(gameId, group);
            return gameId;
        }

        return null; // Not enough players yet
    }

    public static String generateSequentialGameId() {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String newGameId = "game00"; // default fallback

        try {
            connection = DatabaseManager.getConnection();
            String query = "SELECT MAX(CAST(SUBSTRING(gameid, 5) AS UNSIGNED)) AS max_id FROM games";
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();

            int nextId = 1;
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                if (!rs.wasNull()) {
                    nextId = maxId + 1;
                }
            }

            newGameId = String.format("game%02d", nextId); // Ensures 2-digit formatting (e.g., game01, game02...)

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return newGameId;
    }

    public static Set<String> getPlayersInGame(String gameId) {
        return gameSessions.getOrDefault(gameId, new HashSet<>());
    }

    public static Set<String> getAllActiveGameIds() {
        return new HashSet<>(gameSessions.keySet());
    }

    public static GameStatus[] checkWaitingGameStatus() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<GameStatus> statuses = new ArrayList<>();

        try {
            connection = DatabaseManager.getConnection();
            stmt = connection.createStatement();

            // Fetch the latest created game (assumes `gameid` or timestamp reflects order)
            rs = stmt.executeQuery("SELECT gameid, status FROM games ORDER BY gameid DESC LIMIT 1");

            if (rs.next()) {
                String gameId = rs.getString("gameid");
                String status = rs.getString("status");
                GameStatus gs = new GameStatus(gameId, status);
                statuses.add(gs);  // Always return the latest game
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return statuses.toArray(new GameStatus[0]);
    }

    private static final String INSERT_GAME_SQL =
            "INSERT INTO games (gameid, timecreated, status) VALUES (?, ?, ?)";

    /**
     * Registers a new game with the given gameId, current timestamp, and default 'waiting' status.
     *
     * @param gameId The unique ID for the game.
     * @throws SQLException if a database access error occurs.
     */
    public static void registerNewGame(String gameId) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_GAME_SQL)) {

            stmt.setString(1, gameId);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(3, "waiting");

            stmt.executeUpdate();
        }
    }

    public static void updateGameStatus(String gameId, String newStatus) throws SQLException {
        String sql = "UPDATE games SET status = ? WHERE gameid = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setString(2, gameId);

            stmt.executeUpdate();
        }
    }

    public static void setGameComplete(String gameId, String winnerUsername) throws SQLException {
        String sql = "UPDATE games SET status = ?, winner = ? WHERE gameid = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "completed");
            stmt.setString(2, winnerUsername);
            stmt.setString(3, gameId);

            stmt.executeUpdate();
        }
    }
}