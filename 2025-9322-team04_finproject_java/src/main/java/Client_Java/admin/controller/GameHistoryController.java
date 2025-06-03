package Client_Java.admin.controller;

import Client_Java.admin.model.DashboardModel;
import Client_Java.admin.model.GameHistoryModel;
import Client_Java.admin.view.DashboardView;
import Client_Java.admin.view.GameHistoryView;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class GameHistoryController {
    private final GameHistoryView view;
    private final GameHistoryModel model;

    public GameHistoryController(GameHistoryView view, GameHistoryModel model) {
        this.view = view;
        this.model = model;
        initializeController();
        updateGameList();
    }

    private void initializeController() {
        view.getBackButton().addActionListener(e -> {
            view.dispose();
            new DashboardController(new DashboardModel(), new DashboardView());
        });

        // Window control from HeaderTemplate
        view.getHeader().getMinimizeBtn().addActionListener(e ->
                view.getFrame().setExtendedState(JFrame.ICONIFIED));
        view.getHeader().getMaximizeBtn().addActionListener(e ->
                toggleMaximize());
        view.getHeader().getCloseBtn().addActionListener(e ->
                System.exit(0));
    }

    private void toggleMaximize() {
        if (view.getFrame().getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            view.getFrame().setExtendedState(JFrame.NORMAL);
            view.getHeader().getMaximizeBtn().setText("□");
        } else {
            view.getFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
            view.getHeader().getMaximizeBtn().setText("❐");
        }
    }

    private void updateGameList() {
        List<Map<String, Object>> gameSessions = model.displayAvailableGames();
        view.populateGameTable(gameSessions);
    }
}