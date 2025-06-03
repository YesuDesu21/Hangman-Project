package Server_Java.dao;

import Server_Java.manager.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GameServiceDAO {
    // Maps player username to their current round guesses
    private static final Map<String, Set<Character>> playerGuesses = new HashMap<>();
    private static final Map<String, Integer> wrongGuesses = new HashMap<>();
    private static final Map<String, String> activeWords = new HashMap<>();

    public static boolean isWordGuessed(String username) {
        String word = activeWords.get(username);
        Set<Character> guesses = playerGuesses.get(username);
        for (char c : word.toCharArray()) {
            if (!guesses.contains(c)) return false;
        }
        return true;
    }

    public static void createGameSessionDAO() {
        Connection connection = null;
        PreparedStatement insertStmt = null;

        try {
            connection = DatabaseManager.getConnection();

            // Step 1: Get latest numeric ID
            String getMaxIdQuery = "SELECT MAX(CAST(SUBSTRING(gameid, 5) AS UNSIGNED)) AS max_id FROM games";
            PreparedStatement maxIdStmt = connection.prepareStatement(getMaxIdQuery);
            ResultSet rs = maxIdStmt.executeQuery();

            int nextId = 1; // default if no records
            if (rs.next()) {
                int maxId = rs.getInt("max_id");
                if (!rs.wasNull()) {
                    nextId = maxId + 1;
                }
            }
            rs.close();
            maxIdStmt.close();

            String newGameId = String.format("game%02d", nextId);

            String createGameSessionQuery = "INSERT INTO games (gameid, timecreated, status) VALUES (?, NOW(), 'waiting')";
            insertStmt = connection.prepareStatement(createGameSessionQuery);
            insertStmt.setString(1, newGameId);

            int rowsInserted = insertStmt.executeUpdate();

            if (rowsInserted > 0) {
                System.out.println("✔ Created game session with ID: " + newGameId);
            } else {
                System.out.println("⚠ Game session was not created.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (insertStmt != null) insertStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void createGameDetailsDAO(){}

    public static void createRoundDAO(String gameId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseManager.getConnection();

            // Count existing rounds for this gameId
            String countSql = "SELECT COUNT(*) FROM rounds WHERE gameid = ?";
            pstmt = conn.prepareStatement(countSql);
            pstmt.setString(1, gameId);
            rs = pstmt.executeQuery();

            int roundNumber = 1;
            if (rs.next()) {
                roundNumber = rs.getInt(1) + 1;
            }

            String roundId = String.format("round%03d", roundNumber);

            // Insert round with guess = ''
            String insertSql = "INSERT INTO rounds (roundid, gameid, roundnumber, guess, starttime, endtime, status, winner) " +
                    "VALUES (?, ?, ?, '', NOW(), NULL, 'ongoing', '')";
            pstmt.close();
            pstmt = conn.prepareStatement(insertSql);
            pstmt.setString(1, roundId);
            pstmt.setString(2, gameId);
            pstmt.setInt(3, roundNumber);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("✓ Round created successfully: " + roundId);
            } else {
                System.out.println("✘ Failed to create round.");
            }

        } catch (SQLException e) {
            System.err.println("⚠ SQL Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                System.err.println("⚠ Error closing resources: " + ex.getMessage());
            }
        }
    }

    public static void updateGameDAO(String status) {
        Connection connection = null;
        PreparedStatement updateStmt = null;

        try {
            connection = DatabaseManager.getConnection();

            String updateQuery = "UPDATE games " +
                    "SET status = ? " +
                    "WHERE timecreated = (" +
                    "    SELECT MAX(timecreated) FROM (" +
                    "        SELECT timecreated FROM games" +
                    "    ) AS temp" +
                    ")";

            updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setString(1, status.toLowerCase());  // ensure lowercase

            int rowsUpdated = updateStmt.executeUpdate();
            System.out.println("Updated rows: " + rowsUpdated);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (updateStmt != null) updateStmt.close();
                if (connection != null) connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void updateRoundDAO(String status){}

    public static String readRoundWordDAO(){
        return "";
    }

    public static void deleteGameSessionDAO() {
        Connection connection = null;
        PreparedStatement deleteStmt = null;

        try {
            connection = DatabaseManager.getConnection();

            String deleteQuery =
                    "DELETE FROM games " +
                            "WHERE timecreated = (" +
                            "   SELECT MAX(timecreated) FROM (" +
                            "       SELECT timecreated FROM games" +
                            "   ) AS temp" +
                            ")";

            deleteStmt = connection.prepareStatement(deleteQuery);
            int rowsDeleted = deleteStmt.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("✔ Most recent game session deleted.");
            } else {
                System.out.println("⚠ No game session was deleted.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (deleteStmt != null) deleteStmt.close();
                if (connection != null) connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}