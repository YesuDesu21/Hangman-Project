package Server_Java.implementation;

import Server_Java.dao.LeaderboardServiceDAO;
import compilations.LeaderboardServicePOA;
import compilations.PlayerScore;

import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of the CORBA LeaderboardService.
 * Provides functionality to retrieve the top players and to record a player's win.
 */
public class LeaderboardServiceImpl extends LeaderboardServicePOA {

    /**
     * Retrieves the list of top players from the database, sorted by number of games won.
     *
     * @return an array of PlayerScore objects containing usernames and games won;
     *         returns an empty array if a database error occurs
     */
    @Override
    public PlayerScore[] getTopPlayers() {
        try {
            List<LeaderboardServiceDAO.PlayerScoreData> topPlayers =
                    LeaderboardServiceDAO.getTopPlayers();

            PlayerScore[] result = new PlayerScore[topPlayers.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = new PlayerScore(
                        topPlayers.get(i).username,
                        (short)topPlayers.get(i).gamesWon
                );
            }
            return result;
        } catch (SQLException e) {
            System.err.println("Database error while fetching leaderboard: " + e.getMessage());
            return new PlayerScore[0];
        }
    }

    /**
     * Records a win for the specified player in the database.
     *
     * @param username the username of the player whose win is to be recorded
     */
    @Override
    public void recordPlayerWin(String username) {
        try {
            LeaderboardServiceDAO.recordPlayerWin(username);
            System.out.println("Recorded win for player: " + username);
        } catch (SQLException e) {
            System.err.println("Error recording win for player " + username + ": " + e.getMessage());
        }
    }
}