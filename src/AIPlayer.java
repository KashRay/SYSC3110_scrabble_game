import java.util.*;

public class AIPlayer extends Player {


    public AIPlayer(String name) {
        super(name);
    }

    private boolean canFormWord(String word, Map<Character, Integer> letterFrequency, int blanksAvailable) {
        Map<Character, Integer> wordFrequency = new HashMap<>();
        for (char c : word.toCharArray()) {
            wordFrequency.put(c, wordFrequency.getOrDefault(c, 0) + 1);
        }

        for (Map.Entry<Character, Integer> entry : letterFrequency.entrySet()) {
            char letter = entry.getKey();
            int countNeeded = entry.getValue();
            int countHave = letterFrequency.getOrDefault(letter, 0);

            if (countHave < countNeeded) {
                int missing = countNeeded - countHave;
                if (blanksAvailable >= missing) blanksAvailable -= missing;
                else return false;
            }
        }
        return true;
    }

    public List<String> getFormableWords(List<String> wordlist) {
        List<String> formableWords =  new ArrayList<>();

        Map<Character, Integer> letterFrequency = new HashMap<>();
        int blankCount = 0;

        for (Tile tile : hand) {
            char letter = tile.getLetter();
            if (letter == ' ') blankCount++;
            else letterFrequency.put(letter, letterFrequency.getOrDefault(letter, 0) + 1);
        }
        for (String word : wordlist) if (canFormWord(word, letterFrequency, blankCount)) formableWords.add(word);

        return formableWords;
    }
}
