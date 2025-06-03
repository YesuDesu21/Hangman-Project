package Client_Java.admin.model;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ManagePlayersModel {
    private List<Player> players;

    public ManagePlayersModel() {
        players = new ArrayList<>();
//        initializeSampleData();
    }

    public void initializeSampleData() {
        addPlayer(new Player("jasmine", 10, "espejo"));
        addPlayer(new Player("jasper", 9, "doria"));
        addPlayer(new Player("alvin", 8, "tolentino"));
        addPlayer(new Player("alwin", 7, "garcia"));
        addPlayer(new Player("jr", 6, "geronimo"));
        addPlayer(new Player("josh", 5, "calulut"));
        addPlayer(new Player("kiel", 4, "delapena"));
    }

    public DefaultTableModel getTableModel() {
        String[] columns = {"#", "Username", "Wins", "Password"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            model.addRow(new Object[]{i + 1, p.getUsername(), p.getWins(), p.getPassword()});
        }
        return model;
    }

    // Add this getter method
    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void deletePlayer(int index) {
        if (index >= 0 && index < players.size()) {
            players.remove(index);
        }
    }

    public void updatePlayer(int index, Player updatedPlayer) {
        if (index >= 0 && index < players.size()) {
            players.set(index, updatedPlayer);
        }
    }
    //for committing
    public Player getPlayer(int index) {
        if (index >= 0 && index < players.size()) {
            return players.get(index);
        }
        return null;
    }
    //for committing
    public static class Player {
        private String username;
        private int wins;
        private String password;

        public Player(String username, int wins, String password) {
            this.username = username;
            this.wins = wins;
            this.password = password;
        }

        public String getUsername() { return username; }
        public int getWins() { return wins; }
        public String getPassword() { return password; }
        public void setUsername(String username) { this.username = username; }
        public void setWins(int wins) { this.wins = wins; }
        public void setPassword(String password) { this.password = password; }
    }
}