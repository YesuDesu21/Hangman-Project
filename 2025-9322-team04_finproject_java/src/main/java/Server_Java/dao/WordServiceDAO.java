package Server_Java.dao;

import Server_Java.manager.DatabaseManager;
import compilations.GameNotFoundException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class WordServiceDAO {
    private static final String WORDS_FILE = "resources/words.txt"; // adjust path if needed
    private static final Set<String> usedWords = new HashSet<>();
    private static final List<String> allWords = new ArrayList<>();

    static {
        loadWords();
    }

    private static void loadWords() {
        try (BufferedReader br = new BufferedReader(new FileReader(WORDS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                allWords.add(line.trim().toLowerCase());
            }
            System.out.println("Loaded " + allWords.size() + " words.");
        } catch (IOException e) {
            System.err.println("Error loading words file: " + e.getMessage());
        }
    }

    public static String getRoundWord(String gameId, String roundid) throws GameNotFoundException {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // Step 1: Read all words from the text file
            List<String> allWords = Files.readAllLines(Paths.get("src/main/resources/words.txt"))
                    .stream()
                    .map(String::trim)
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.toList());

            System.out.println("Loaded " + allWords.size() + " words from words.txt");
            System.out.println("Starting to pick a word for gameId: " + gameId);

            if (allWords.isEmpty()) {
                System.out.println("No words found in the word list.");
                throw new GameNotFoundException("No words found in the word list.");
            }

            // Step 2: Connect to the database
            connection = DatabaseManager.getConnection();
            System.out.println("Connected to database for gameId: " + gameId);

            Random rand = new Random();
            String chosenWord = null;

            // Step 3: Loop to pick a random word and check if it is used
            while (!allWords.isEmpty()) {
                // Pick a random word
                int index = rand.nextInt(allWords.size());
                String word = allWords.get(index);
                System.out.println("Trying word: \"" + word + "\" for gameId: " + gameId);

                // Query to check if this word has already been used in this game
                String query = "SELECT 1 FROM rounds WHERE gameid = ? AND LOWER(guess) = LOWER(?) LIMIT 1";
                stmt = connection.prepareStatement(query);
                stmt.setString(1, gameId);
                stmt.setString(2, word);

                rs = stmt.executeQuery();

                if (!rs.next()) {
                    // Word not used yet
                    chosenWord = word;
                    System.out.println("Selected word: \"" + chosenWord + "\" for gameId: " + gameId);
                    rs.close();
                    stmt.close();
                    break;
                } else {
                    System.out.println("Word \"" + word + "\" already used for gameId: " + gameId);
                }

                // Word was used, remove it from list and try again
                rs.close();
                stmt.close();
                allWords.remove(index);
            }

            if (chosenWord == null) {
                System.out.println("No unused words available for this game: " + gameId);
                throw new GameNotFoundException("No unused words available for this game.");
            }
            return chosenWord;
        } catch (IOException e) {
            System.out.println("Error reading word file: " + e.getMessage());
            throw new GameNotFoundException("Error reading word file: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
            throw new GameNotFoundException("SQL error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (connection != null) connection.close();
                System.out.println("Closed DB resources.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}