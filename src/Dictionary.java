import java.util.*;
import java.io.*;

public class Dictionary {
    private final Set<String> words;

    public Dictionary() {
        words = new HashSet<>();
    }

    /**
     * Loads words from a text file into the dictionary.
     * Each line in the file is treated as a separate word. Leading and trailing spaces are removed,
     * and all words are converted to uppercase for consistency. Empty lines are ignored.
     *
     * @param filename the path to the text file containing words to load
     */
    public void loadFromFile(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                // Read each line, remove extra spaces, and convert to uppercase
                String word = scanner.nextLine().trim().toUpperCase();
                if (!word.isEmpty()) words.add(word);
            }
            System.out.println("Loaded " + words.size() + " words from " + filename);
        } catch (IOException e) {
            // Print an error message if the file could not be read
            System.err.println("Error loading dictionary: " + e.getMessage());
        }
    }

    /**
     * Checks if the given word exists in the dictionary.
     * This method performs a case-insensitive lookup by converting the input word to uppercase
     * before checking for membership.
     *
     * @param word the word to check
     * @return true if the word exists in the dictionary; {@code false} otherwise
     */
    public boolean isValidWord(String word) {
        return words.contains(word.toUpperCase());
    }
}
