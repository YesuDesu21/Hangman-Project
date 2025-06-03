package Client_Java.player.controller;

import Client_Java.player.model.LeaderboardsModel;
import Client_Java.player.view.LeaderboardsView;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class LeaderboardsController {
    private final LeaderboardsModel model;
    private final LeaderboardsView view;
    private final Runnable returnAction;

    // Updated constructor to accept return action
    public LeaderboardsController(Runnable returnAction) {
        this.model = new LeaderboardsModel();
        List<Map<String, Object>> leaderboardData = model.getLeaderboardData();
        this.view = new LeaderboardsView(leaderboardData);
        this.returnAction = returnAction;
        setupActions();
    }

    private void setupActions() {
        view.getMinimizeBtn().addActionListener(e -> view.setState(JFrame.ICONIFIED));
        view.getCloseBtn().addActionListener(e -> System.exit(0));
        view.getMaximizeBtn().addActionListener(e -> toggleMaximize());

        view.getBackButton().addActionListener(e -> {
            view.setVisible(false);
            if (returnAction != null) {
                returnAction.run();
            }
            view.dispose();
        });
    }
    private void toggleMaximize() {
        int state = view.getExtendedState();
        boolean isCurrentlyMaximized = (state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;

        if (isCurrentlyMaximized) {
            view.setExtendedState(JFrame.NORMAL);
            view.setSize(1280, 720);
            view.setLocationRelativeTo(null);
        } else {
            view.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        view.updateMaximizeButton(!isCurrentlyMaximized);
    }

    public void showLeaderboard() {
        view.showView();
    }

    public void recordPlayerWin(String username) {
        model.recordPlayerWin(username);
    }
}