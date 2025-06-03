package Client_Java.player.model;

import Client_Java.player.PlayerCorbaManager;

public class GameProperModel {
    private String wordToGuess;
    private boolean[] revealedLetters;
    private int lives;
    private int timerSeconds;
    private int remainingGuesses;

    private final PlayerCorbaManager corbaManager;

    public GameProperModel(String word) {
        this.corbaManager = PlayerCorbaManager.getInstance();
        this.wordToGuess = word.toUpperCase();
        this.revealedLetters = new boolean[word.length()];
        this.lives = 5;  // Default lives
        this.timerSeconds = corbaManager.getAdminService().getRoundDuration();
    }

    public String getWordToGuess() {
        return wordToGuess;
    }


    public boolean[] getRevealedLetters() {
        return revealedLetters;
    }


    public boolean isLetterInWord(char letter) {
        boolean found = false;
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == letter) {
                revealedLetters[i] = true;  // Mark letter as revealed
                found = true;
            }
        }
        return found;
    }


    public int getLives() {
        return lives;
    }


    public void decrementLives() {
        if (lives > 0) lives--;
    }


    public int getTimerSeconds() {
        return timerSeconds;
    }


    public void decrementTimer() {
        timerSeconds--;
    }


    public boolean isGameOver() {
        return lives <= 0 || isWordFullyRevealed();
    }


    public boolean isWordFullyRevealed() {
        for (boolean b : revealedLetters) {
            if (!b) return false;
        }
        return true;
    }

    public void setWordToGuess(String word) {
        this.wordToGuess = word.toUpperCase();
        this.revealedLetters = new boolean[word.length()];
    }

    public void resetGame() {
        this.lives = 5;
        this.timerSeconds = 30;
        this.revealedLetters = new boolean[wordToGuess.length()];
    }

    public void updateWordState(String maskedWord, String actualWord, short remainingGuesses) {
        this.wordToGuess = actualWord;
        this.remainingGuesses = remainingGuesses;
        this.revealedLetters = new boolean[actualWord.length()];
        for (int i = 0; i < maskedWord.length(); i++) {
            if (maskedWord.charAt(i) != '_') {
                revealedLetters[i] = true;
            }
        }
    }

    public boolean isSessionValid() {
        String sessionId = PlayerCorbaManager.getInstance().getSessionId();
        return PlayerCorbaManager.getInstance().getLoginService().isSessionActive(sessionId);
    }
}
