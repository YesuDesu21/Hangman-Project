package Server_Java.dao;

import Server_Java.manager.DatabaseManager;
import compilations.GameSession;
import compilations.GameSessionObject;
import compilations.PlayerAccount;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminServiceDAO {
    private static int waitTime;
    private static int roundDuration;

    public static void updateGameWaitTime(int seconds) {
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = DatabaseManager.getConnection();
            stmt = connection.createStatement();
            stmt.executeUpdate("UPDATE gameconfig SET waittime = " + seconds);
            waitTime = seconds;
            log("[DATABASE] Wait Time set to " + seconds);
        } catch (Exception e) {
            log("[DATABASE] Error updating wait time: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                log("[DATABASE] Error closing resources: " + e.getMessage());
            }
        }
    }

    public static void updateRoundDuration(int seconds) {
        Connection connection = null;
        Statement stmt = null;
        try {
            connection = DatabaseManager.getConnection();
            stmt = connection.createStatement();
            stmt.executeUpdate("UPDATE gameconfig SET roundduration = " + seconds);
            roundDuration = seconds;
            log("[DATABASE] Round Duration set to " + seconds);
        } catch (Exception e) {
            log("[DATABASE] Error updating round duration: " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                log("[DATABASE] Error closing resources: " + e.getMessage());
            }
        }
    }

    public static GameSessionObject[] getGameHistory() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseManager.getConnection();
            stmt = connection.createStatement();
            resultSet = stmt.executeQuery("SELECT * FROM games");

            List<GameSessionObject> gameList = new ArrayList<>();

            while (resultSet.next()) {
                String gameID = resultSet.getString("gameid");
                String timeCreated = resultSet.getString("timecreated");
                String status = resultSet.getString("status");

                GameSessionObject gameSession = new GameSessionObject();
                gameSession.gameId = gameID;
                gameSession.timeCreated = timeCreated;
                gameSession.status = status;

                gameList.add(gameSession);
            }

            return gameList.toArray(new GameSessionObject[0]);
        } catch (Exception e) {
            log("[DATABASE] Error retrieving game history: " + e.getMessage());
            return new GameSessionObject[0];
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                log("[DATABASE] Error closing resources: " + e.getMessage());
            }
        }
    }

    public static boolean createPlayer(String username, String password) throws SQLException {
        Connection connection = null;
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseManager.getConnection();

            String checkQuery = "SELECT COUNT(*) FROM players WHERE username = ?";
            checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            rs = checkStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) > 0) {
                return false;
            }

            String playerId = generatePlayerId(connection);

            String insertQuery = "INSERT INTO players (playerid, username, password, gameswon, loggedin) VALUES (?, ?, ?, ?, ?)";
            insertStmt = connection.prepareStatement(insertQuery);
            insertStmt.setString(1, playerId);
            insertStmt.setString(2, username);
            insertStmt.setString(3, password);
            insertStmt.setInt(4, 0);
            insertStmt.setInt(5, 0);

            insertStmt.executeUpdate();
            log("[DATABASE] Created player account: " + username + " (ID: " + playerId + ")");
            return true;
        } finally {
            if (rs != null) rs.close();
            if (checkStmt != null) checkStmt.close();
            if (insertStmt != null) insertStmt.close();
            if (connection != null) connection.close();
        }
    }

    private static String generatePlayerId(Connection connection) throws SQLException {
        int nextId = 1;
        String playerId;

        while (true) {
            playerId = String.format("player%03d", nextId);
            if (!playerIdExists(connection, playerId)) {
                break;
            }
            nextId++;
        }
        return playerId;
    }

    private static boolean playerIdExists(Connection connection, String playerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM players WHERE playerid = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, playerId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static boolean removePlayer(String username) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseManager.getConnection();
            String query = "DELETE FROM players WHERE username = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);

            int rowsAffected = stmt.executeUpdate();
            log("[DATABASE] Removed player: " + username);
            return rowsAffected > 0;
        } finally {
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    public static boolean updatePlayerCredentials(String currentUsername, String newUsername, String newPassword) throws SQLException {
        Connection connection = null;
        PreparedStatement checkUserStmt = null;
        PreparedStatement checkNewUsernameStmt = null;
        PreparedStatement updateStmt = null;
        ResultSet rs = null;

        try {
            connection = DatabaseManager.getConnection();

            String checkUserQuery = "SELECT COUNT(*) FROM players WHERE username = ?";
            checkUserStmt = connection.prepareStatement(checkUserQuery);
            checkUserStmt.setString(1, currentUsername);
            rs = checkUserStmt.executeQuery();
            rs.next();
            if (rs.getInt(1) == 0) {
                return false;
            }

            if (!currentUsername.equals(newUsername)) {
                String checkNewUsernameQuery = "SELECT COUNT(*) FROM players WHERE username = ?";
                checkNewUsernameStmt = connection.prepareStatement(checkNewUsernameQuery);
                checkNewUsernameStmt.setString(1, newUsername);
                ResultSet rs2 = checkNewUsernameStmt.executeQuery();
                rs2.next();
                if (rs2.getInt(1) > 0) {
                    rs2.close();
                    return false;
                }
                rs2.close();
            }

            String updateQuery = "UPDATE players SET username = ?, password = ? WHERE username = ?";
            updateStmt = connection.prepareStatement(updateQuery);
            updateStmt.setString(1, newUsername);
            updateStmt.setString(2, newPassword);
            updateStmt.setString(3, currentUsername);

            int rowsAffected = updateStmt.executeUpdate();
            log("[DATABASE] Updated credentials for player: " + currentUsername + " -> " + newUsername + ", " + newPassword);
            return rowsAffected > 0;
        } finally {
            if (rs != null) rs.close();
            if (checkUserStmt != null) checkUserStmt.close();
            if (checkNewUsernameStmt != null) checkNewUsernameStmt.close();
            if (updateStmt != null) updateStmt.close();
            if (connection != null) connection.close();
        }
    }

    public static List<PlayerAccount> searchPlayerAccounts(String keyword) throws SQLException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<PlayerAccount> results = new ArrayList<>();

        try {
            connection = DatabaseManager.getConnection();

            String query = "SELECT username, password, gameswon FROM players WHERE username LIKE ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, "%" + keyword + "%");
            rs = stmt.executeQuery();

            while (rs.next()) {
                PlayerAccount account = new PlayerAccount();
                account.username = rs.getString("username");
                account.password = rs.getString("password");
                account.gamesWon = (short) rs.getInt("gameswon");
                results.add(account);
            }
            return results;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (connection != null) connection.close();
        }
    }

    public static short getWaitingTime() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        short waitTime = 0;

        try {
            connection = DatabaseManager.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT waittime FROM gameconfig");

            if (rs.next()) {
                waitTime = rs.getShort("waittime");
            }
        } catch (Exception e) {
            log("[DATABASE] Error fetching wait time: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (Exception ex) {
                log("[DATABASE] Error closing resources: " + ex.getMessage());
            }
        }
        return waitTime;
    }

    public static short getRoundDuration() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        short roundDuration = 0;

        try {
            connection = DatabaseManager.getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("SELECT roundduration FROM gameconfig");

            if (rs.next()) {
                roundDuration = rs.getShort("roundduration");
            }
        } catch (Exception e) {
            log("[DATABASE] Error fetching round duration: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
            } catch (Exception ex) {
                log("[DATABASE] Error closing resources: " + ex.getMessage());
            }
        }
        return roundDuration;
    }

    public static void log(String message) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + timestamp + "] [SERVER] " + message);
    }
}