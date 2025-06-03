package Client_Java.player.controller;

import Client_Java.player.model.*;
import Client_Java.player.view.*;
import Client_Java.player.PlayerCorbaManager;
import compilations.*;

import javax.swing.*;

public class HomepageController {
    private final HomepageModel model;
    private final HomepageView view;
    private final PlayerCorbaManager corbaManager;

    public HomepageController(HomepageModel model, HomepageView view) {
        this.model = model;
        this.view = view;

        // Initialize CORBA manager with proper parameters
//        String[] corbaArgs = {"-ORBInitialPort", "1050", "-ORBInitialHost", "localhost"};
        this.corbaManager = PlayerCorbaManager.getInstance();

        setupActions();
        startSessionValidationTimer();
    }
    private void setupActions() {
        view.getStartGameBtn().addActionListener(e -> {
            view.getFrame().setVisible(false); // Hide instead of dispose
            openWaitingRoom();
        });

        view.getViewLeaderboardBtn().addActionListener(e -> {
            view.getFrame().setVisible(false); // Hide instead of dispose
            LeaderboardsController controller = new LeaderboardsController(this::returnToHomepage);
            controller.showLeaderboard();
        });

        view.getLogoutBtn().addActionListener(e -> logout());

        view.getMinimizeBtn().addActionListener(e -> view.getFrame().setState(JFrame.ICONIFIED));
        view.getCloseBtn().addActionListener(e -> System.exit(0));
        view.getMaximizeBtn().addActionListener(e -> toggleMaximize());
    }

    private void returnToHomepage() {
        SwingUtilities.invokeLater(() -> {
            view.getFrame().setVisible(true);
        });
    }

    private void openWaitingRoom() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Get all necessary CORBA services
                GameService gameService = corbaManager.getGameService();
                WordService wordService = corbaManager.getWordService();
                AdminService adminService = corbaManager.getAdminService();

                String username = model.getUsername();
                if (username == null || username.isEmpty()) {
                    JOptionPane.showMessageDialog(null,
                            "Username is not set. Please login first.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int waitTime = adminService.getJoinTimeout() > 0 ?
                        adminService.getJoinTimeout() : 10;

                WaitingRoomModel wrModel = new WaitingRoomModel(username, waitTime);
                WaitingRoomView wrView = new WaitingRoomView();

                // Pass all necessary services to WaitingRoomController
                new WaitingRoomController(wrModel, wrView, () -> {
                    view.getFrame().setVisible(true);
                }, gameService, wordService);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to GameService.\nDetails: " + e.getMessage(),
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                view.getFrame().setVisible(true);
            }
        });
    }

    public void openGameProper(String wordToPlay, int currentRound,
                               WordService wordService, GameService gameService,
                               String username, String gameId) {
        SwingUtilities.invokeLater(() -> {
            GameProperModel model = new GameProperModel(wordToPlay);
            GameProperView view = new GameProperView(wordToPlay);
            new GameProperController(model, view, corbaManager, username, currentRound, gameId);
        });
    }

    private void logout() {
        view.getFrame().dispose();

        String sessionId = corbaManager.getInstance().getSessionId();
        corbaManager.getInstance().getLoginService().logoutPlayer(sessionId);

        PlayerLoginView view = new PlayerLoginView();
        PlayerLoginModel model = new PlayerLoginModel();
        new PlayerLoginController(model, view);
    }

    private void toggleMaximize() {
        JFrame frame = view.getFrame();
        int state = frame.getExtendedState();
        boolean isCurrentlyMaximized = (state & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;

        if (isCurrentlyMaximized) {
            frame.setExtendedState(JFrame.NORMAL);
            frame.setSize(1280, 720);
            frame.setLocationRelativeTo(null);
        } else {
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        view.updateMaximizeButton(!isCurrentlyMaximized);
    }

    private void startSessionValidationTimer() {
        Timer sessionCheckTimer = new Timer(5000, e -> {
            try {
                if (!model.isSessionValid()) {
                    String sessionId = corbaManager.getInstance().getSessionId();
                    corbaManager.getInstance().getLoginService().logoutPlayer(sessionId); // Invalidate session

                    JOptionPane.showMessageDialog(view.getFrame(),
                            "Your session has expired or you have been logged out elsewhere.",
                            "Session Expired",
                            JOptionPane.WARNING_MESSAGE);

                    view.getFrame().dispose(); // Close current view
                    PlayerLoginView view = new PlayerLoginView();
                    PlayerLoginModel model = new PlayerLoginModel();
                    new PlayerLoginController(model, view);
                }
            } catch (Exception ex) {
                System.err.println("Error checking session validity: " + ex.getMessage());
            }
        });
        sessionCheckTimer.start();
    }

}