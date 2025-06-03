
package Client_Java.player.model;


import Client_Java.player.PlayerCorbaManager;
import compilations.PlayerScore;
import compilations.LeaderboardService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class LeaderboardsModel {
    private final LeaderboardService leaderboardService;

    public LeaderboardsModel() {
        this.leaderboardService = PlayerCorbaManager.getInstance().getLeaderboardService();
    }

    public List<Map<String, Object>> getLeaderboardData() {
        List<Map<String, Object>> data = new ArrayList<>();

        try {
            PlayerScore[] topPlayers = leaderboardService.getTopPlayers();
            for (PlayerScore player : topPlayers) {
                data.add(createEntry(player.username, player.gamesWon));
            }


        } catch (Exception e) {
            System.err.println("Error getting leaderboard data: " + e.getMessage());
            // Fallback to sample data
            data.add(createEntry("wordMaster007", 1));
            data.add(createEntry("noobie3", 0));
        }
        return data;
    }


    public void recordPlayerWin(String username) {
        try {
            leaderboardService.recordPlayerWin(username);
            System.out.println("Win recorded for: " + username);
        } catch (Exception e) {
            System.err.println("Error recording win: " + e.getMessage());
        }
    }


    private Map<String, Object> createEntry(String name, int score) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", name);
        entry.put("score", score);
        return entry;
    }
}

