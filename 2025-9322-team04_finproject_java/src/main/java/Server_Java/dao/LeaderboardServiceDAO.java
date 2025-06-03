package Server_Java.dao;

import Server_Java.manager.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardServiceDAO {
    private static final String GET_TOP_PLAYERS_SQL =
            "SELECT username, gameswon FROM players ORDER BY gameswon DESC LIMIT 10";

    private static final String RECORD_WIN_SQL =
            "UPDATE players SET gameswon = gameswon + 1 WHERE username = ?";

    public static List<PlayerScoreData> getTopPlayers() throws SQLException {
        List<PlayerScoreData> topPlayers = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_TOP_PLAYERS_SQL)) {

            while (rs.next()) {
                topPlayers.add(new PlayerScoreData(
                        rs.getString("username"),
                        rs.getInt("gameswon")
                ));
            }
        }
        return topPlayers;
    }

    public static void recordPlayerWin(String username) throws SQLException {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(RECORD_WIN_SQL)) {

            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    public static class PlayerScoreData {
        public final String username;
        public final int gamesWon;

        public PlayerScoreData(String username, int wins) {
            this.username = username;
            this.gamesWon = wins;
        }
    }
}