package Client_Java.admin.controller;

import Client_Java.admin.model.*;
import Client_Java.admin.view.*;
import Server_Java.implementation.GameManagerServiceImpl;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class DashboardController {
    private DashboardModel model;
    private DashboardView view;

    public DashboardController(DashboardModel model, DashboardView view) {
        this.model = model;
        this.view = view;
        setupListeners();
        startSessionValidationTimer();
    }
    //for committing
    private void setupListeners() {
        // Window control buttons
        view.getMinimizeBtn().addActionListener(e ->
                view.getFrame().setExtendedState(JFrame.ICONIFIED));

        view.getMaximizeBtn().addActionListener(e -> toggleMaximize());

        view.getCloseBtn().addActionListener(e -> System.exit(0));

        view.getLogOutBtn().addActionListener(e -> {
            view.getFrame().dispose();
            AdminLoginView loginView = new AdminLoginView();
            AdminLoginModel loginModel = new AdminLoginModel();
            new AdminLoginController(loginModel, loginView);
        });

        // Window resize handling
        view.getFrame().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                view.getHeader().updateHeaderSize(view.getFrame().getWidth());
                view.updateMaximizeButton(model.isMaximized());
            }
        });

        // Navigation
        view.getTimeConfigBtn().addActionListener(e -> {
            view.getFrame().dispose();
            TimeConfigView timeConfigView = new TimeConfigView();
            TimeConfigModel timeConfigModel = new TimeConfigModel();
            new TimeConfigController(timeConfigView, timeConfigModel);
        });

        view.getManagePlayersBtn().addActionListener(e -> {
            view.getFrame().dispose();
            ManagePlayersView managePlayersView = new ManagePlayersView();
            ManagePlayersModel managePlayersModel = new ManagePlayersModel();
            new ManagePlayersController(managePlayersView, managePlayersModel);
        });

        view.getGameHistBtn().addActionListener(e -> {
            view.getFrame().dispose();
            GameHistoryView gameHistoryView = new GameHistoryView();
            GameHistoryModel gameHistoryModel = new GameHistoryModel();
            new GameHistoryController(gameHistoryView, gameHistoryModel);
        });

        view.getSearchPlayersBtn().addActionListener(e -> {
            view.getFrame().dispose();
            SearchPlayerView searchPlayerView = new SearchPlayerView();
            SearchPlayerModel searchPlayerModel = new SearchPlayerModel();
            new SearchPlayerController(searchPlayerModel, searchPlayerView);
        });
    }

    private void startSessionValidationTimer() {
        Timer sessionCheckTimer = new Timer(5000, e -> {
            try {
                if (!model.isSessionValid()) {
                    JOptionPane.showMessageDialog(view.getFrame(),
                            "Your session has expired or you have been logged out elsewhere.",
                            "Session Expired",
                            JOptionPane.WARNING_MESSAGE);
                    System.exit(0); // Or navigate to login screen
                }
            } catch (Exception ex) {
                System.err.println("Error checking session validity: " + ex.getMessage());
            }
        });
        sessionCheckTimer.start();
    }


    private void toggleMaximize() {
        if (model.isMaximized()) {
            view.getFrame().setExtendedState(JFrame.NORMAL);
            view.getFrame().setSize(800, 600);
            view.getFrame().setLocationRelativeTo(null);
            model.setMaximized(false);
        } else {
            view.getFrame().setExtendedState(JFrame.MAXIMIZED_BOTH);
            model.setMaximized(true);
        }
        view.updateMaximizeButton(model.isMaximized());
    }
}