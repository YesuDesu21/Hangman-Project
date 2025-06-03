package Server_Java.implementation;

import Server_Java.dao.WordServiceDAO;
import compilations.WordServicePOA;
import compilations.WordServicePackage.GameNotFoundException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the CORBA WordService.
 * Manages game words by providing unique words per game session, caching the current word,
 * and tracking used words to avoid repetition.
 * Also interacts with the database via DAO for round-specific word selection.
 */
public class WordServiceImpl extends WordServicePOA {
    private final List<String> words = new ArrayList<>();
    private final Map<String, Set<String>> usedWordsPerGame = new ConcurrentHashMap<>();  // Use ConcurrentHashMap for thread safety
    private final Map<String, String> currentWordPerGame = new ConcurrentHashMap<>();

    /**
     * Constructs the WordServiceImpl and loads words from the words.txt file.
     * Populates the internal word list used across game sessions.
     */
    public WordServiceImpl() {
        loadWordsFromFile();
    }

    /**
     * Returns a new word for the specified game session. If a word is already cached and unused,
     * it is returned; otherwise, a new word is randomly selected from the word list and marked as used.
     *
     * @param gameId the unique identifier for the game session
     * @return a new or cached word for the game, or "NO_WORD_AVAILABLE" if none are left
     */
    @Override
    public synchronized String getNewWord(String gameId) {
        if (currentWordPerGame.containsKey(gameId)) {
            String cachedWord = currentWordPerGame.get(gameId);
            if (!usedWordsPerGame.getOrDefault(gameId, new HashSet<>()).contains(cachedWord)) {
                log("Returning cached word for game [" + gameId + "]: " + cachedWord);
                return cachedWord;
            } else {
                log("Cached word [" + cachedWord + "] already used for game [" + gameId + "], selecting a new one.");
            }
        }

        Set<String> usedWords = usedWordsPerGame.getOrDefault(gameId, new HashSet<>());
        List<String> availableWords = new ArrayList<>();

        for (String word : words) {
            if (!usedWords.contains(word)) {
                availableWords.add(word);
            }
        }

        log("Available words for game [" + gameId + "]: " + availableWords.size() + " words");

        if (availableWords.isEmpty()) {
            log("No available words for game: " + gameId);
            return "NO_WORD_AVAILABLE";
        }

        Random random = new Random();
        String word = availableWords.get(random.nextInt(availableWords.size()));
        currentWordPerGame.put(gameId, word);
        markWordAsUsed(word, gameId);
        log("Selected NEW word for game [" + gameId + "]: " + word);

        return word;
    }

    /**
     * Marks the specified word as used in the context of the given game session.
     *
     * @param word the word to mark as used
     * @param gameId the ID of the game session
     */
    @Override
    public synchronized void markWordAsUsed(String word, String gameId) {
        usedWordsPerGame.computeIfAbsent(gameId, k -> new HashSet<>()).add(word);
        log("Marked word as used: " + word + " (gameId=" + gameId + ")");
    }

    /**
     * Loads words from the "words.txt" file located in the classpath.
     * Populates the internal `words` list used for random word selection.
     */
    private void loadWordsFromFile() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("words.txt"))
        )) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    words.add(line.trim());
                }
            }

            if (words.isEmpty()) {
                log("The words file is EMPTY or NOT FOUND.");
            } else {
                log("Loaded " + words.size() + " words from words.txt");
            }
        } catch (Exception e) {
            log("Failed to load words: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the currently cached word for a given game session.
     *
     * @param gameId the game session ID
     * @return the current word, or "NO_WORD_AVAILABLE" if none is cached
     */
    @Override
    public synchronized String getCurrentWord(String gameId) {
        String currentWord = currentWordPerGame.get(gameId);
        if (currentWord == null) {
            log("No current word found for gameId: " + gameId);
            return "NO_WORD_AVAILABLE";
        }
        return currentWord;
    }

    /**
     * Clears the cached current word for the specified game session.
     *
     * @param gameId the game session ID whose word is to be cleared
     */
    @Override
    public synchronized void clearCurrentWord(String gameId) {
        currentWordPerGame.remove(gameId);
    }

    /**
     * Fetches a word for the specified game round by querying the database.
     * This method is expected to ensure that the word is unique per round.
     *
     * @param gameid the ID of the game session
     * @param roundid the ID of the round within the game
     * @return the selected word for the round
     * @throws GameNotFoundException if the game or round context is invalid
     */
    @Override
    public String getRoundWord(String gameid, String roundid) throws GameNotFoundException {
        try {
            return WordServiceDAO.getRoundWord(gameid, roundid);
        } catch (compilations.GameNotFoundException e) {
            throw new RuntimeException(e);
        }
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