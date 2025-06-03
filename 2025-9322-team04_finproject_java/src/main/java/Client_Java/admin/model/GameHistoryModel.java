package Client_Java.admin.model;

import Client_Java.admin.AdminCorbaManager;
import compilations.AdminService;
import compilations.GameSessionObject;

import java.util.*;

public class GameHistoryModel {
    private final AdminService adminService;

    public GameHistoryModel() {
        // Obtain the AdminService CORBA stub from manager
        this.adminService = AdminCorbaManager.getInstance().getAdminService();

    }
    public List<Map<String, Object>> displayAvailableGames() {
        List<Map<String, Object>> gameDataList = new ArrayList<>();

        try {
            GameSessionObject[] gameSessions = adminService.getGameHistory();
            System.out.println("Number of game sessions fetched: " + gameSessions.length);

            for (GameSessionObject session : gameSessions) {
                gameDataList.add(createEntry(session));
            }

        } catch (Exception e) {
            System.err.println("Error calling getGameHistory: " + e.getMessage());
            e.printStackTrace();
        }

        return gameDataList;
    }
    private Map<String, Object> createEntry(GameSessionObject session) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("gameId", session.gameId);
        entry.put("timeCreated", session.timeCreated);
        entry.put("status", session.status);
        return entry;
    }
}
