package Server_Java.session;

import compilations.WordMaskInfo;
import compilations.WordService;

import java.util.*;

/**
 * Represents a single game session identified by a unique gameId.
 * Tracks the current word, rounds, player guesses, remaining attempts,
 * round winners, and overall session status including score tracking.
 */
public class GameSession {
    private final String gameId;
    private String word;
    private int currentRound = 1;
    private final Map<String, Set<Character>> playerGuesses = new HashMap<>();
    private final Map<String, Integer> playerRemainingGuesses = new HashMap<>();
    private final Map<String, Integer> sessionRoundWins = new HashMap<>();
    private boolean roundCompleted = false;
    private String roundWinner = null;

    /**
     * Creates a new game session with the specified gameId and starting word.
     *
     * @param gameId the unique identifier for this game session
     * @param word the initial word to guess, must be non-null and non-empty
     * @throws IllegalArgumentException if the word is null or empty
     */
    public GameSession(String gameId, String word) {
        this.gameId = gameId;
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("Word cannot be null or empty");
        }
        this.word = word.toUpperCase();
    }

    /**
     * Registers a new player in this session, initializing their guesses,
     * remaining guesses, and session wins if not already registered.
     *
     * @param username the player's username
     */
    public void registerPlayer(String username) {
        playerGuesses.putIfAbsent(username, new HashSet<>());
        playerRemainingGuesses.putIfAbsent(username, 5);
        sessionRoundWins.putIfAbsent(username, 0);
    }

    /**
     * Records a player's guess of a letter. If the letter was not guessed before
     * and is not in the word, decrements the player's remaining guesses.
     * If the game is over, no guesses are processed.
     *
     * @param username the player making the guess
     * @param letter the guessed letter (case-insensitive)
     */
    public void guessLetter(String username, char letter) {
        if (isGameOver()) return;
        letter = Character.toUpperCase(letter);
        Set<Character> guesses = playerGuesses.get(username);
        if (guesses == null) return;

        if (guesses.add(letter) && !word.contains(String.valueOf(letter))) {
            playerRemainingGuesses.computeIfPresent(username, (u, g) -> g - 1);
        }
    }

    /**
     * Returns a WordMaskInfo object representing the current masked word
     * for the specified player, including revealed letters and remaining guesses.
     *
     * @param username the player's username
     * @return a WordMaskInfo with masked word, actual word, and remaining guesses
     */
    public WordMaskInfo getMaskedInfo(String username) {
        Set<Character> guesses = playerGuesses.getOrDefault(username, Collections.emptySet());
        StringBuilder masked = new StringBuilder();
        for (char c : word.toCharArray()) {
            masked.append(guesses.contains(c) ? c : "_");
        }
        WordMaskInfo info = new WordMaskInfo();
        info.maskedWord = masked.toString();
        info.actualWord = word;
        info.remainingGuesses = (short) (int) playerRemainingGuesses.getOrDefault(username, 0);
        return info;
    }

    /**
     * Returns the number of guesses remaining for the specified player.
     *
     * @param username the player's username
     * @return remaining guesses count, or 0 if player not registered
     */
    public int getRemainingGuesses(String username) {
        return playerRemainingGuesses.getOrDefault(username, 0);
    }

    /**
     * Returns the current word for this game session.
     *
     * @return the word to guess in uppercase
     */
    public String getWord() {
        return word;
    }

    /**
     * Returns the unique identifier for this game session.
     *
     * @return the game session ID
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Returns an unmodifiable set of players registered in this session.
     *
     * @return set of registered player usernames
     */
    public Set<String> getRegisteredPlayers() {
        return Collections.unmodifiableSet(playerGuesses.keySet());
    }

    /**
     * Returns the current round number of this game session.
     *
     * @return the current round (starting at 1)
     */
    public int getCurrentRound() {
        return currentRound;
    }

    /**
     * Advances to the next round with a new word obtained from the WordService.
     * Resets guesses and lives for all players. Does nothing if the session
     * has been completed or the game is over.
     *
     * @param wordService the WordService used to fetch the new word
     */
    public synchronized void nextRound(WordService wordService) {
        if (roundCompleted || isGameOver()) {
            System.out.println("Next round skipped: session already completed or game over.");
            this.roundWinner = null;
            return;

        }

        wordService.clearCurrentWord(gameId);
        String newWord = wordService.getNewWord(gameId);

        if (newWord == null || newWord.equals("NO_WORD_AVAILABLE")) {
            System.out.println("No new word available. Keeping current word.");
            return;
        }

        wordService.markWordAsUsed(newWord, gameId);
        this.word = newWord.toUpperCase();
        this.currentRound++;
        this.roundWinner = null;

        // reset guesses and lives for all players
        for (String player : playerGuesses.keySet()) {
            playerGuesses.put(player, new HashSet<>());
            playerRemainingGuesses.put(player, 5);
        }
    }

    /**
     * Checks if the specified player has guessed all letters in the current word.
     *
     * @param username the player's username
     * @return true if the player has guessed every letter, false otherwise
     */
    public boolean isWordFullyGuessed(String username) {
        Set<Character> guesses = playerGuesses.get(username);
        if (guesses == null) return false;
        for (char c : word.toCharArray()) {
            if (!guesses.contains(c)) return false;
        }
        return true;
    }

    /**
     * Checks if any player in the session has fully guessed the current word.
     *
     * @return true if at least one player has guessed the word, false otherwise
     */
    public boolean isWordGuessedByAnyPlayer() {
        return getRegisteredPlayers().stream().anyMatch(this::isWordFullyGuessed);
    }

    /**
     * Indicates whether the current round is completed.
     *
     * @return true if the round is completed, false otherwise
     */
    public synchronized boolean isRoundCompleted() {
        return roundCompleted;
    }

    /**
     * Sets the round completion status.
     *
     * @param completed true to mark the round as completed, false otherwise
     */
    public void setRoundCompleted(boolean completed) {
        this.roundCompleted = completed;
    }

    /**
     * Records a round win for the specified player. If a player reaches
     * 3 round wins, marks the session as completed and the player as winner.
     * Does nothing if the round winner is already set.
     *
     * @param username the player to credit the round win
     */
    public void incrementSessionRoundWin(String username) {
        if (roundWinner != null) return;

        roundWinner = username;
        int wins = sessionRoundWins.merge(username, 1, Integer::sum);

        if (wins >= 3) {
            roundCompleted = true;
        }
    }

    /**
     * Returns an unmodifiable map of player usernames to their total round wins.
     *
     * @return map of players to their round win counts
     */
    public Map<String, Integer> getSessionRoundWins() {
        return Collections.unmodifiableMap(sessionRoundWins);
    }

    /**
     * Indicates whether the session game is over (a player has at least 3 wins).
     *
     * @return true if a player has reached 3 round wins, false otherwise
     */
    public boolean isGameOver() {
        return sessionRoundWins.values().stream().anyMatch(w -> w >= 3);
    }

    /**
     * Returns the username of the session winner (player with ≥3 round wins),
     * or "No winner" if none has reached that threshold.
     *
     * @return the winning player's username or "No winner"
     */
    public String getWinner() {
        return sessionRoundWins.entrySet().stream()
                .filter(e -> e.getValue() >= 3)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("No winner");
    }

    /**
     * Returns the username of the winner of the current round, or null if none.
     *
     * @return current round winner username or null
     */
    public String getRoundWinner() {
        return roundWinner;
    }

    /**
     * Sets the winner of the current round.
     *
     * @param username the username of the player to set as round winner
     */
    public void setRoundWinner(String username) {
        this.roundWinner = username;
    }
}