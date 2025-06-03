package Server_Java.implementation;

import Server_Java.dao.GameManagerServiceDAO;
import Server_Java.dao.GameServiceDAO;
import Server_Java.dao.LeaderboardServiceDAO;
import Server_Java.session.GameManager;
import Server_Java.session.GameSession;
import compilations.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the GameService CORBA interface.
 *
 * This class handles the main game logic and client requests,
 * coordinating with the WordService and GameManager to manage game sessions,
 * player actions, and game state.
 */
public class GameServiceImpl extends GameServicePOA {
    private final GameManager gameManager = GameManager.getInstance();
    private final WordService wordService;
    private final Map<String, Map<Integer, Set<String>>> donePlayersPerGamePerRound = new HashMap<>();
    private final Map<String, Integer> playerScores = new ConcurrentHashMap<>();

    /**
     * Constructs a new GameServiceImpl with the specified WordService.
     * Initializes the game manager with the provided word service and
     * loads player scores from the database.
     *
     * @param wordService the WordService used to fetch game words and manage word state
     */
    public GameServiceImpl(WordService wordService) {
        this.wordService = wordService;
        gameManager.setWordService(wordService);
        loadPlayerScoresFromDB();
    }

    /**
     * Registers the player to join a game session. A session will be created or joined if one is available.
     *
     * @param username the player's username
     * @throws NoOpponentFoundException if no opponent is currently available
     */
    @Override
    public void requestToJoinGame(String username) throws NoOpponentFoundException {
        gameManager.joinGame(username);
    }

    /**
     * Retrieves the current masked word information for the player, along with the game ID and actual word.
     *
     * @param username the player's username
     * @return WordMaskInfo containing masked word, game ID, and actual word
     * @throws GameNotFoundException if no session is found for the player
     */
    @Override
    public WordMaskInfo getCurrentWordMask(String username) throws GameNotFoundException {
        GameSession session = gameManager.getSession(username);
        String actualWord = session.getWord();
        if (actualWord == null || actualWord.trim().isEmpty()) {
            throw new GameNotFoundException();
        }
        WordMaskInfo info = session.getMaskedInfo(username);
        info.actualWord = actualWord;
        info.gameId = session.getGameId();
        return info;
    }

    /**
     * Processes the player's guessed letter. If the guess is valid, it checks for round completion or game over state.
     *
     * @param username the player's username
     * @param letter the guessed character
     * @throws InvalidGuessException if the guessed character is not a letter
     * @throws GameNotFoundException if no session is found for the player
     */
    @Override
    public void submitLetterGuess(String username, char letter) throws InvalidGuessException, GameNotFoundException {
        if (!Character.isLetter(letter)) {
            throw new InvalidGuessException();
        }
        GameSession session = gameManager.getSession(username);
        session.guessLetter(username, Character.toUpperCase(letter));

        if (session.isGameOver()) {
            log("Game is over. Skipping further round processing.");

            try {
                String winner = session.getWinner();
                log("Game is over. Winner: " + winner);
                GameManagerServiceDAO.setGameComplete(session.getGameId(), winner);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return;
        }

        if (session.isWordFullyGuessed(username)) {
            log(username + " has fully guessed the word!");

            synchronized (session) {
                if (session.getRoundWinner() == null) {
                    session.setRoundWinner(username);
                    log(username + " was the first to guess the word!");
                }
            }

            setPlayerDone(username, (short) session.getCurrentRound(), true);
        }

        if (!session.isGameOver() && session.isWordGuessedByAnyPlayer()) {
            String gameId = session.getGameId();
            short round = (short) session.getCurrentRound();

            log("A player guessed the word. Waiting for current round to end...");

            if (!session.isRoundCompleted()) {
                wordService.clearCurrentWord(gameId);
                session.nextRound(wordService);
                session.setRoundCompleted(false);
                donePlayersPerGamePerRound.get(gameId).remove((int) round);
            }
        }
    }

    /**
     * Returns the number of remaining incorrect guesses for the player.
     *
     * @param username the player's username
     * @return the number of remaining guesses
     * @throws GameNotFoundException if no session is found for the player
     */
    @Override
    public short getRemainingGuesses(String username) throws GameNotFoundException {
        return (short) gameManager.getSession(username).getRemainingGuesses(username);
    }

    /**
     * Returns a textual summary of the player's current game progress.
     *
     * @param username the player's username
     * @return a string describing the player's game status
     * @throws GameNotFoundException if no session is found for the player
     */
    @Override
    public String getGameProgressStatus(String username) throws GameNotFoundException {
        return gameManager.getStatus(username);
    }

    /**
     * Returns the number of players currently joined in a session.
     *
     * @return the current player count
     */
    @Override
    public short getCurrentPlayerCount() {
        return (short) gameManager.getJoinedPlayerCount();
    }

    /**
     * Retrieves a list of all players in the current session.
     *
     * @return an array of player usernames
     * @throws GameNotFoundException if no session is found
     */
    @Override
    public String[] getAllPlayers() throws GameNotFoundException {
        List<String> players = gameManager.getAllPlayers();
        return players.toArray(new String[0]);
    }

    /**
     * Marks a player as done for a round and awards a point if they were the first to guess correctly.
     *
     * @param username the player's username
     * @param round the round number
     * @param guessedCorrectly whether the player guessed the word correctly
     * @throws GameNotFoundException if no session is found
     */
    @Override
    public synchronized void setPlayerDone(String username, short round, boolean guessedCorrectly) throws GameNotFoundException {
        GameSession session = gameManager.getSession(username);
        String gameId = session.getGameId();

        log(">>> Entering setPlayerDone for " + username + ", round=" + round + ", guessedCorrectly=" + guessedCorrectly);
        log("    Current round winner: " + session.getRoundWinner());

        Map<Integer, Set<String>> roundsMap = donePlayersPerGamePerRound.computeIfAbsent(
                gameId, k -> new HashMap<>());
        Set<String> donePlayersSet = roundsMap.computeIfAbsent(
                (int) round, r -> new HashSet<>());
        donePlayersSet.add(username);

        log("Player marked done - Player: " + username +
                ", Game: " + gameId + ", Round: " + round +
                ", Done players: " + donePlayersSet);

        if (guessedCorrectly) {
            synchronized (session) {
                if (session.getRoundWinner() == null) {
                    log(username + " was the first to guess the word and got a point!");
                    session.incrementSessionRoundWin(username);
                    incrementScoreAndCheckWinner(username);
                } else {
                    log(username + " guessed correctly but wasn't the first. No point awarded.");
                }
            }
        }
    }

    /**
     * Checks if all players have completed the given round.
     *
     * @param username the player's username
     * @param round the round number
     * @return true if all players are done for the round, false otherwise
     * @throws GameNotFoundException if no session is found
     */
    @Override
    public synchronized boolean areAllPlayersDone(String username, short round) throws GameNotFoundException {
        GameSession session = gameManager.getSession(username);
        String gameId = session.getGameId();
        Set<String> registeredPlayers = session.getRegisteredPlayers();
        Map<Integer, Set<String>> roundsMap = donePlayersPerGamePerRound.get(gameId);
        if (roundsMap == null) return false;

        Set<String> donePlayersThisRound = roundsMap.get((int) round);
        if (donePlayersThisRound == null) return false;

        return donePlayersThisRound.containsAll(registeredPlayers) &&
                registeredPlayers.containsAll(donePlayersThisRound);
    }

    /**
     * Advances to the next round if all players are done or the word has been guessed.
     *
     * @param username the player's username
     * @param round the current round number
     * @return true if round advanced, false otherwise
     * @throws GameNotFoundException if no session is found
     */
    public synchronized boolean advanceToNextRoundIfReady(String username, short round) throws GameNotFoundException {
        GameSession session = gameManager.getSession(username);

        if (session.isGameOver()) {
            log("Game " + session.getGameId() + "over. No further rounds will be started.");
            try {
                String winner = session.getWinner();
                log("Game " + session.getGameId() + " is over. Winner: " + winner);
                GameManagerServiceDAO.setGameComplete(session.getGameId(), winner);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }

        String gameId = session.getGameId();
        Set<String> registeredPlayers = session.getRegisteredPlayers();
        Map<Integer, Set<String>> gameRounds = donePlayersPerGamePerRound.getOrDefault(gameId, Collections.emptyMap());
        Set<String> donePlayersThisRound = gameRounds.getOrDefault((int) round, Collections.emptySet());

        boolean allDone = donePlayersThisRound.containsAll(registeredPlayers);

        if (session.isWordGuessedByAnyPlayer() || allDone) {
            log("Proceeding to the next round.");
            wordService.clearCurrentWord(gameId);
            session.nextRound(wordService);
            session.setRoundCompleted(false);
            donePlayersPerGamePerRound.get(gameId).remove((int) round);
            return true;
        }

        return false;
    }

    /**
     * Retrieves the number of total games won by each player.
     *
     * @return an array of PlayerScore objects
     */
    @Override
    public PlayerScore[] getAllPlayerScores() {
        List<PlayerScore> scoreList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : playerScores.entrySet()) {
            PlayerScore score = new PlayerScore();
            score.username = entry.getKey();
            score.gamesWon = entry.getValue().shortValue();
            scoreList.add(score);
        }
        return scoreList.toArray(new PlayerScore[0]);
    }

    /**
     * Retrieves the current round number for a specific game.
     *
     * @param gameId the game session ID
     * @return the current round number
     * @throws GameNotFoundException if the game session is not found
     */
    @Override
    public short getCurrentRound(String gameId) throws GameNotFoundException {
        GameSession session = gameManager.getSessionByGameId(gameId);
        if (session == null) {
            throw new GameNotFoundException("Game not found for ID: " + gameId);
        }
        return (short) session.getCurrentRound();
    }

    /**
     * Loads player win data from the database into the internal score map.
     */
    public void loadPlayerScoresFromDB() {
        try {
            List<LeaderboardServiceDAO.PlayerScoreData> scores = LeaderboardServiceDAO.getTopPlayers();
            for (LeaderboardServiceDAO.PlayerScoreData score : scores) {
                playerScores.put(score.username, score.gamesWon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Increments the round win count for a player and checks if they have won the game.
     *
     * @param playerName the player's username
     * @return true if the player has won the game, false otherwise
     */
    public synchronized boolean incrementScoreAndCheckWinner(String playerName) {
        GameSession session;
        try {
            session = gameManager.getSession(playerName);
        } catch (GameNotFoundException e) {
            log("No session found for player: " + playerName);
            return false;
        }

        session.incrementSessionRoundWin(playerName);
        int roundWins = session.getSessionRoundWins().getOrDefault(playerName, 0);
        log(playerName + " has " + roundWins + " round wins in current session.");

        if (roundWins >= 3) {
            int newTotalWins = playerScores.getOrDefault(playerName, 0) + 1;
            playerScores.put(playerName, newTotalWins);

            log(playerName + " WON the game! Updating DB. Total wins: " + newTotalWins);
            try {
                LeaderboardServiceDAO.recordPlayerWin(playerName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }

        return false;
    }

    /**
     * Checks if the current game session is over.
     *
     * @return true if the game has ended, false otherwise
     */
    @Override
    public boolean isGameOver() {
        GameSession session = gameManager.getCurrentSession();
        if (session == null) return true;

        boolean over = session.getSessionRoundWins()
                .values()
                .stream()
                .anyMatch(wins -> wins >= 3);

        if (over) {
            gameManager.markGameEnded();
            gameManager.delayedReset();
        }

        return over;
    }

    /**
     * Retrieves the username of the game winner, if available.
     *
     * @return the winner's username, or a message if the game is still in progress or was reset
     */
    @Override
    public String getWinner() {
        if (!gameManager.hasGameEnded()) {
            return "Game still in progress.";
        }

        GameSession session = gameManager.getCurrentSession();
        if (session == null) {
            return "No winner — session was reset.";
        }

        return session.getSessionRoundWins()
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No winner");
    }

    /**
     * Returns a summary of final scores after the game ends.
     *
     * @return a formatted string of scores or a message if the game is still ongoing
     */
    @Override
    public String getScoreSummary() {
        if (!gameManager.hasGameEnded()) {
            return "Game still in progress.";
        }

        GameSession session = gameManager.getCurrentSession();
        if (session == null) {
            return "No scores — session was reset.";
        }

        StringBuilder sb = new StringBuilder("Final Scores:\n");
        session.getSessionRoundWins()
                .forEach((user, wins) ->
                        sb.append(user).append(": ").append(wins).append("\n")
                );
        return sb.toString();
    }

    /**
     * Creates a new game session entry in the database.
     */
    @Override
    public void createGameSession() {
        GameServiceDAO.createGameSessionDAO();
    }

    /**
     * Creates a new game details entry in the database.
     * @throws GameNotFoundException if session not found
     */
    @Override
    public void createGameDetails() throws GameNotFoundException {
        GameServiceDAO.createGameDetailsDAO();
    }

    /**
     * Creates a new round entry in the database.
     *
     * @param gameId the game ID
     * @throws GameNotFoundException if game not found
     */
    @Override
    public void createRound(String gameId) throws GameNotFoundException {
        GameServiceDAO.createRoundDAO(gameId);
    }

    /**
     * Updates the game status in the database.
     *
     * @param status the new status string
     * @throws GameNotFoundException if game not found
     */
    @Override
    public void updateGame(String status) throws GameNotFoundException {
        GameServiceDAO.updateGameDAO(status);
    }

    /**
     * Updates the round status in the database.
     *
     * @param status the new status string
     * @throws GameNotFoundException if game not found
     */
    @Override
    public void updateRound(String status) throws GameNotFoundException {
        GameServiceDAO.updateRoundDAO(status);
    }

    /**
     * Reads the current round's word from the database.
     *
     * @return the word as a string
     * @throws GameNotFoundException if round not found
     */
    @Override
    public String readRoundWord() throws GameNotFoundException {
        return GameServiceDAO.readRoundWordDAO();
    }

    /**
     * Deletes the current game session from the database.
     *
     * @throws GameNotFoundException if session not found
     */
    @Override
    public void deleteGameSession() throws GameNotFoundException {
        GameServiceDAO.deleteGameSessionDAO();
    }

    /**
     * Gets the current round ID for a given game.
     *
     * @param gameId the game ID
     * @return currently returns an empty string
     * @throws GameNotFoundException if game not found
     */
    @Override
    public String getRoundId(String gameId) throws GameNotFoundException {
        return "";
    }

    /**
     * Utility method for logging server-side actions with a timestamp.
     *
     * @param message the message to log
     */
    public static void log(String message) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        System.out.println("[" + timestamp + "] [SERVER] " + message);
    }
}