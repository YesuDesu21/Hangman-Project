package Client_Java.player.controller;

import Client_Java.player.model.GameProperModel;
import Client_Java.player.model.HomepageModel;
import Client_Java.player.model.PlayerLoginModel;
import Client_Java.player.view.GameProperView;
import Client_Java.player.view.HomepageView;
import Client_Java.player.PlayerCorbaManager;
import Client_Java.player.view.PlayerLoginView;
import compilations.*;
import javax.swing.*;

public class GameProperController {
    private final String gameId;
    private final String username;
    private final GameProperModel model;
    private final GameProperView view;
    private final PlayerCorbaManager corbaManager;

    private Timer gameTimer;
    private boolean hasSubmitted = false;
    private boolean nextRoundStarted = false;

    private int currentRound;
    private final int maxRounds = 3;

    public GameProperController(GameProperModel model, GameProperView view,
                                PlayerCorbaManager corbaManager,
                                String username, int currentRound, String gameId) {
        this.model = model;
        this.view = view;
        this.corbaManager = corbaManager;
        this.username = username;
        this.currentRound = currentRound;
        this.gameId = gameId;

        view.setRound(currentRound);
        setupKeyboard();
        setupTimer();
        setupHeaderButtons();
        startSessionValidationTimer();
    }

    private void setupKeyboard() {
        for (char c = 'A'; c <= 'Z'; c++) {
            JButton key = view.getKeyButton(c);
            if (key != null) {
                final char guessChar = c;
                key.addActionListener(e -> handleGuess(guessChar, key));
            }
        }
    }

    private void handleGuess(char guess, JButton key) {
        if (hasSubmitted || model.getLives() <= 0) return;

        key.setEnabled(false);
        boolean isCorrect = model.isLetterInWord(guess);

        if (isCorrect) {
            updateRevealedLetters();
        } else {
            model.decrementLives();
            updateHearts();
        }

        updateRevealedLetters();

        if (!hasSubmitted && model.isWordFullyRevealed()) {
            hasSubmitted = true;
            gameTimer.stop();
            notifyPlayerDone();
            view.showWaitingForOthersDialog();
        }

        if (!hasSubmitted && (model.getTimerSeconds() <= 0 || model.getLives() <= 0)) {
            hasSubmitted = true;
            gameTimer.stop();
            notifyPlayerDone();
            view.showWaitingForOthersDialog();
        }
    }

    private void updateRevealedLetters() {
        boolean[] revealed = model.getRevealedLetters();
        JLabel[] labels = view.getLetterLabels();
        for (int i = 0; i < labels.length; i++) {
            if (revealed[i]) {
                labels[i].setText(String.valueOf(model.getWordToGuess().charAt(i)));
            }
        }
    }

    private void updateHearts() {
        JLabel[] hearts = view.getHeartLabels();
        int livesLeft = model.getLives();
        for (int i = 0; i < hearts.length; i++) {
            hearts[i].setVisible(i < livesLeft);
        }
    }

    private void setupTimer() {
        gameTimer = new Timer(1000, e -> {
            model.decrementTimer();
            view.getTimerLabel().setText("Time Remaining: " + model.getTimerSeconds());

            if (!hasSubmitted && (model.getTimerSeconds() <= 0 || model.getLives() <= 0)) {
                gameTimer.stop();
                hasSubmitted = true;
                notifyPlayerDone();
                view.showWaitingForOthersDialog();
            }
        });
        gameTimer.start();
    }

    private void setupHeaderButtons() {
        view.getMinimizeBtn().addActionListener(e -> view.getFrame().setState(JFrame.ICONIFIED));
        view.getMaximizeBtn().addActionListener(e -> {
            JFrame frame = view.getFrame();
            if (frame.getExtendedState() == JFrame.MAXIMIZED_BOTH) {
                frame.setExtendedState(JFrame.NORMAL);
            } else {
                frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            }
        });
        view.getCloseBtn().addActionListener(e -> {
            gameTimer.stop();
            System.exit(0);
        });
    }

    private void notifyPlayerDone() {
        try {
            boolean guessedCorrectly = model.isWordFullyRevealed();
            GameService gameService = corbaManager.getGameService();
            short serverRoundNumber = gameService.getCurrentRound(gameId);

            gameService.setPlayerDone(username, serverRoundNumber, guessedCorrectly);
            boolean roundAdvanced = gameService.advanceToNextRoundIfReady(username, serverRoundNumber);

            checkAllPlayersDone();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view.getFrame(),
                    "Error notifying server: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkAllPlayersDone() {
        Timer waitTimer = new Timer(1000, null);
        waitTimer.addActionListener(e -> {
            try {
                GameService gameService = corbaManager.getGameService();

                if (gameService.isGameOver()) {
                    waitTimer.stop();
                    showFinalScoresAndReturnHome();
                    return;
                }

                int serverRound = gameService.getCurrentRound(gameId);
                if (serverRound > currentRound) {
                    waitTimer.stop();
                    proceedToNextRound();
                    return;
                }

                if (gameService.areAllPlayersDone(username, (short) currentRound)) {
                    waitTimer.stop();
                    if (currentRound >= maxRounds - 1) {
                        endGame();
                    } else {
                        showFinalWordAndCountdown();
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        waitTimer.start();
    }


    private void showFinalWordAndCountdown() {
        view.hideWaitingDialogAndEnableInput();
        int countdownSeconds = 5;
        final int[] secondsLeft = {countdownSeconds};

        Timer countdownTimer = new Timer(1000, null);
        countdownTimer.addActionListener(e -> {
            if (secondsLeft[0] > 0) {
                view.getTimerLabel().setText("The word was: " + model.getWordToGuess() + "\nNext round in " + secondsLeft[0]--);
            } else {
                countdownTimer.stop();
                waitForNextRoundOnServer();
            }
        });
        countdownTimer.setInitialDelay(0);
        countdownTimer.start();
    }

    private void waitForNextRoundOnServer() {
        Timer waitForServer = new Timer(1000, null);
        waitForServer.addActionListener(e -> {
            try {
                GameService gameService = corbaManager.getGameService();
                int serverRound = gameService.getCurrentRound(gameId);
                if (serverRound > currentRound) {
                    waitForServer.stop();
                    proceedToNextRound();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        waitForServer.start();
    }

    private void proceedToNextRound() {
        if (nextRoundStarted) return;

        nextRoundStarted = true;
        hasSubmitted = false;
        int nextRound = currentRound + 1;

        try {
            WordService wordService = corbaManager.getWordService();
            String newWord = wordService.getCurrentWord(gameId);
            if (newWord == null || newWord.trim().isEmpty()) {
                newWord = "FALLBACK";
            }

            view.getFrame().dispose();

            GameProperModel nextModel = new GameProperModel(newWord);
            GameProperView newView = new GameProperView(newWord);
            new GameProperController(nextModel, newView, corbaManager, username, nextRound, gameId);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view.getFrame(),
                    "Error proceeding to next round: " + e.getMessage());
        }
    }

    private void endGame() {
        view.hideWaitingDialogAndEnableInput();
        JOptionPane.showMessageDialog(view.getFrame(),
                "Game Over!\nThe final word was: " + model.getWordToGuess() +
                        "\nThank you for playing!");
        view.getFrame().dispose();
    }

    private void showFinalScoresAndReturnHome() {
        view.hideWaitingDialogAndEnableInput();
        try {
            GameService gameService = corbaManager.getGameService();
            String scores = gameService.getScoreSummary();
            String winner = gameService.getWinner();

            JOptionPane.showMessageDialog(view.getFrame(),
                    "Game Over!\nWinner: " + winner + "\n\n" + scores,
                    "Final Scores", JOptionPane.INFORMATION_MESSAGE);

            view.getFrame().dispose();

            HomepageModel homepageModel = new HomepageModel();
            homepageModel.setUsername(username);

            HomepageView homepageView = new HomepageView();
            new HomepageController(homepageModel, homepageView);

            homepageView.show();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view.getFrame(),
                    "Error retrieving final scores: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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

