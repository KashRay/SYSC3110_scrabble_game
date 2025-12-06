import java.io.Serializable;
import java.util.*;

public class AIPlayer extends Player implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new AIPlayer as a child of Player
     * 
     * @param name the name of the AIPlayer
     */
    public AIPlayer(String name) {
        super(name);
    }

    /**
     * Determines the score a certain word placement would earn.
     * 
     * @param word              The word to be placed
     * @param row               The starting row of the word
     * @param col               The starting coloumn of the word
     * @param isHorizontal      Whether the word is horizontal or vertical
     * @param board             The board that the word is placed on
     * @param dictionary        The dictionary that contains the list of eligible words
     * @param firstTurn         Checks whether it's the first turn or not
     * @param letterFrequency   A map determining how many of each letter the AIPlayer has
     * @param totalBlanks       The number of blanks in the AIPlayer's hand
     * @return  The score the mainWord would earn
     */
    private int getSimulatedScore(String word, int row, int col, boolean isHorizontal, Board board, Dictionary dictionary, boolean firstTurn, Map<Character, Integer> letterFrequency, int totalBlanks) {
        //Geometry and overlap check
        if (!board.isValidPlacement(word, row, col, isHorizontal, firstTurn)) return -1;

        //Check which tiles we need to place from our hand
        List<Tile> tempTiles = new ArrayList<>();
        Map<Character, Integer> needed = new HashMap<>();
        int r = row, c = col;

        for (int i = 0; i < word.length(); i++) {
            if (board.getTile(r, c) == null) {
                char letter = word.charAt(i);
                needed.put(letter, needed.getOrDefault(letter, 0) + 1);

                //Create a temporary tile for simulation
                Tile tile = new Tile(letter, ScrabbleLetters.get(letter).getScore());
                tile.setCoords(r, c);
                tempTiles.add(tile);
            }
            if (isHorizontal) c++;
            else r++;
        }

        if (tempTiles.isEmpty()) return -1; //Move uses 0 tiles

        //Check if our hand actually has these tiles
        int blanks = totalBlanks;
        for (Map.Entry<Character, Integer> entry : needed.entrySet()) {
            int have = letterFrequency.getOrDefault(entry.getKey(), 0);
            if (have < entry.getValue()) {
                int missing = entry.getValue() - have;
                if (blanks >= missing) blanks -= missing;
                else return -1;
            }
        }

        //Simulate placing down tiles and ask game logic to validate
        for (Tile tile : tempTiles) board.placeTile(tile.getX(), tile.getY(), tile);

        int score = -1;
        try { //Check everything
            score = Game.analyzeMove(board, dictionary, tempTiles, firstTurn).totalScore();
        }
        catch (IllegalArgumentException _) { //Move is invalid
        }
        finally {
            for (Tile tile : tempTiles) board.removeTile(tile.getX(), tile.getY());
        }

        return score;
    }

    /**
     * Determines the best mainWord the AIPlayer can make
     * 
     * @param dictionary    The dictionary that contains the list of eligible words
     * @param board         The board that the word is placed on
     * @param firstTurn     Checks whether it's the first turn or not
     * @return  The mainWord that would earn the most points
     */
    public AIMove getBestMove(Dictionary dictionary, Board board, boolean firstTurn) {
        Set<String> wordlist = dictionary.getWords();
        AIMove bestAIMove = null;
        int maxScore = -1;

        //Pre-calculate hand frequency
        Map<Character, Integer> letterFrequency = new HashMap<>();
        int blankCount = 0;

        for (Tile tile : this.getHand()) {
            if (tile.getScore() == 0 || tile.getLetter() == ' ') blankCount++;
            else letterFrequency.put(tile.getLetter(), letterFrequency.getOrDefault(tile.getLetter(), 0) + 1);
        }

        //Iterate through every word in the dictionary
        for (String word : wordlist) {
            for (int row = 0; row < Board.SIZE; row++) {
                for (int col = 0; col < Board.SIZE; col++) {
                    //Try horizontal
                    int horizontalScore = getSimulatedScore(word, row, col, true, board, dictionary, firstTurn, letterFrequency, blankCount);
                    if (horizontalScore > maxScore) {
                        maxScore = horizontalScore;
                        bestAIMove = new AIMove(word, row, col, true);
                    }
                    //Try vertical
                    int verticalScore = getSimulatedScore(word, row, col, false, board, dictionary, firstTurn, letterFrequency, blankCount);
                    if (verticalScore > maxScore) {
                        maxScore = verticalScore;
                        bestAIMove = new AIMove(word, row, col, false);
                    }

                }
            }
        }

        return bestAIMove;
    }
}
