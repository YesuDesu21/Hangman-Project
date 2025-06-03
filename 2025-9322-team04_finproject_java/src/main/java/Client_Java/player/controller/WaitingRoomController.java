package Client_Java.player.controller;

import Client_Java.player.model.HomepageModel;
import Client_Java.player.model.WaitingRoomModel;
import Client_Java.player.view.HomepageView;
import Client_Java.player.view.WaitingRoomView;
import Client_Java.player.PlayerCorbaManager;
import compilations.GameNotFoundException;
import compilations.GameService;
import compilations.NoOpponentFoundException;
import compilations.WordMaskInfo;
import compilations.WordService;

import javax.swing.*;

public class WaitingRoomController {
    private final WaitingRoomModel model;
    private final WaitingRoomView view;
    private final Runnable returnAction;
    private GameService gameService;
    private final WordService wordService;
    private final int currentRound = 1;

    public WaitingRoomController(WaitingRoomModel model,
                                 WaitingRoomView view,
                                 Runnable returnAction, GameService gameService, WordService wordService) {
        this.model = model;
        this.view = view;
        this.returnAction = returnAction;

        // Initialize both services
        PlayerCorbaManager corbaManager = PlayerCorbaManager.getInstance();
        this.gameService = corbaManager.getGameService();
        this.wordService = corbaManager.getWordService();

        // Show the view (replaced showWaitingRoom() with showView())
        view.showView();
        setupActions();
        startCountdown();
        startPolling();
        startPollingPlayerCount();
    }
    private void setupActions() {
        view.getBackButton().addActionListener(e -> exitWaitingRoom());
        view.getMinimizeBtn().addActionListener(e -> view.setState(JFrame.ICONIFIED));
        view.getCloseBtn().addActionListener(e -> System.exit(0));
        view.getMaximizeBtn().addActionListener(e -> toggleMaximize());
    }

    private void exitWaitingRoom() {
        view.setVisible(false);
        if (returnAction != null) {
            returnAction.run();
        }
        view.dispose();
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
    }

    private void startPolling() {
        new Thread(() -> {
            try {
                // Register player with CORBA game service
                gameService.requestToJoinGame(model.getUsername());

                while (true) {
                    try {
                        String status = gameService.getGameProgressStatus(model.getUsername());
                        SwingUtilities.invokeLater(() -> view.updateStatus("Status: " + status));

                        if ("STARTED".equalsIgnoreCase(status)) {
                            WordMaskInfo wordInfo = gameService.getCurrentWordMask(model.getUsername());
                            if (wordInfo == null || wordInfo.actualWord == null || wordInfo.actualWord.trim().isEmpty()) {
                                SwingUtilities.invokeLater(() -> {
                                    JOptionPane.showMessageDialog(null,
                                            "Server did not send a valid word. Please restart the game.");
                                    exitWaitingRoom();
                                });
                                break;
                            }

                            // Initialize game with CORBA services
                            SwingUtilities.invokeLater(() -> {
                                HomepageModel homepageModel = new HomepageModel();
                                homepageModel.setUsername(model.getUsername());
                                HomepageView homepageView = new HomepageView();

                                // Pass CORBA manager to HomepageController
                                HomepageController homepageController = new HomepageController(
                                        homepageModel,
                                        homepageView
                                );

                                homepageController.openGameProper(
                                        wordInfo.actualWord,
                                        currentRound,
                                        wordService,
                                        gameService,
                                        model.getUsername(),
                                        wordInfo.gameId
                                );
                            });

                            SwingUtilities.invokeLater(() -> view.setVisible(false));
                            break;
                        }
                        Thread.sleep(1000);
                    } catch (org.omg.CORBA.TIMEOUT e) {
                        SwingUtilities.invokeLater(() -> {
                            view.updateStatus("Connection timeout. Retrying...");
                        });
                        // Re-establish CORBA connection
                        String[] orbArgs = new String[0];
                        PlayerCorbaManager.getInstance().reconnect(orbArgs); //

                        this.gameService = PlayerCorbaManager.getInstance().getGameService();
                    }
                }
            } catch (NoOpponentFoundException e) {
                SwingUtilities.invokeLater(() -> view.updateStatus("No opponent found. Try again."));
            } catch (GameNotFoundException e) {
                SwingUtilities.invokeLater(() -> view.updateStatus("Game not found."));
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    view.updateStatus("Error: " + e.getMessage());
                    exitWaitingRoom();
                });
            }
        }).start();
    }
    private void showHomepage(String username) {
        HomepageModel homepageModel = new HomepageModel();
        homepageModel.setUsername(username);

        HomepageView homepageView = new HomepageView();

        HomepageController homepageController = new HomepageController(homepageModel, homepageView);

        homepageView.show();
    }
    private void startCountdown() {
        new Thread(() -> {
            while (model.getCountdownTime() > 0) {
                int timeLeft = model.getCountdownTime();
                SwingUtilities.invokeLater(() -> view.updateCountdownLabel(timeLeft));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                model.decrementCountdownTime();
            }

            SwingUtilities.invokeLater(() ->
                    view.updateStatus("⏰ Countdown finished. Waiting for game to start...")
            );

            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    String status = gameService.getGameProgressStatus(model.getUsername());
                    if (!"STARTED".equalsIgnoreCase(status)) {
                        SwingUtilities.invokeLater(() -> {
                            int option = JOptionPane.showConfirmDialog(
                                    null,
                                    "Not enough players to start the game. Would you like to try again?",
                                    "Game Not Started",
                                    JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.WARNING_MESSAGE
                            );

                            if (option == JOptionPane.OK_OPTION) {
                                exitWaitingRoom(); // This will trigger the returnAction
                            } else {
                                exitWaitingRoom(); // Same behavior for cancel
                            }
                        });
                    }
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(null,
                                "Connection error: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        exitWaitingRoom();
                    });
                }
            }).start();
        }).start();
    }

    private void startPollingPlayerCount() {
        new Thread(() -> {
            while (model.getCountdownTime() > 0) {
                try {
                    short count = gameService.getCurrentPlayerCount();
                    SwingUtilities.invokeLater(() -> view.updatePlayerCount(count));
                    Thread.sleep(1000);
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> view.updatePlayerCount(-1));
                }
            }
        }).start();
    }
}
