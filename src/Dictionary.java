import java.util.*;
import java.io.*;

public class Dictionary {
    private final Set<String> words;

    public Dictionary() {
        words = new HashSet<>();
    }

    public void loadFromFile(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                String word = scanner.nextLine().trim().toUpperCase();
                if (!word.isEmpty()) words.add(word);
            }
            System.out.println("Loaded " + words.size() + " words from " + filename);
        } catch (IOException e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
        }
    }

    public boolean isValidWord(String word) {
        return words.contains(word.toUpperCase());
    }
}
