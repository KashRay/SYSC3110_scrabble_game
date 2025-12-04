import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

/**
 * The Game class encapsulates the logic for a Scrabble game session.
 * It manages players, the game board, the tile bag, word validation, scoring,
 * and communication with the view layer.
 *
 * Implements a basic MVC (Model-View-Controller) pattern where Game
 * serves as the Model, holding all core game state and logic.
 */
public class Game implements Serializable {
    private final Board board;
    private final TileBag tileBag;
    private final Dictionary dictionary;
    private final List<Player> players;
    private int currentPlayer;
    private ArrayList<Tile> placedTiles;
    private transient ArrayList<ScrabbleView> views;
    private Tile selectedTile;
    private int endPasses;
    private boolean firstTurn;
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new Game instance with a new board, tile bag,
     * and dictionary. Initializes player and view lists and loads the word list.
     */
    public Game() {
        board = new Board();
        tileBag = new TileBag();
        dictionary = new Dictionary();
        dictionary.loadFromFile("src/wordlist.txt");
        players = new ArrayList<>();
        currentPlayer = 0;
        placedTiles = new ArrayList<Tile>();
        views = new ArrayList<ScrabbleView>();
        selectedTile = null;
        endPasses = 0;
        firstTurn = true;
    }

    /**
     * Registers a new view to be updated whenever the game state changes.
     *
     * @param view the ScrabbleView implementation to register
     */
    public void addView(ScrabbleView view) {
        views.add(view);
    }

    /**
     * Adds a new player to the game.
     *
     * @param name The name of the player to add.
     */
    public void addPlayer(String name) {
        players.add(new Player(name));
    }

    /**
     * Adds a new AI player to the game
     *
     * @param name The name of the AI player to add.
     */
    public void addAIPlayer(String name) {
        players.add(new AIPlayer(name));
    }

    /**
     * Starts the game by distributing tiles to each player.
     */
    public void startGame() {
        int numPlayers;
        int numAIPlayers;

        this.updateViewsTopText("Welcome to SCRABBLE!");

        while (true) {
            try {
                // Prompt user for number of players
                numPlayers = Integer.parseInt(JOptionPane.showInputDialog("Please enter the number of players (1-4): "));
                if (numPlayers < 1 || numPlayers > 4) {
                    JOptionPane.showMessageDialog(null, "ERROR! Please enter a number between 1 and 4.");
                    continue;
                }
                break;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "ERROR! Please enter an Integer.");
            }
        }

        while (true) {
            try {
                //Prompt user for number of AI players
                numAIPlayers = Integer.parseInt(JOptionPane.showInputDialog("Please enter the number of AI players (0-3): "));
                if (numAIPlayers < 0 || numAIPlayers > 3) {
                    JOptionPane.showMessageDialog(null, "ERROR! Please enter a number between 0 and 3.");
                    continue;
                }
                break;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "ERROR! Please enter an Integer.");
            }
        }

        //Gather player names
        for (int i = 1; i <= numPlayers; i++) {
            String name = JOptionPane.showInputDialog("Enter a name for Player " + i + ": ");
            this.addPlayer(name);
        }

        //Create AI players
        for (int i = 1; i <= numAIPlayers; i++) {
            this.addAIPlayer("AI " + i);
        }

        for (Player player : players) {
            player.addTile(tileBag);
        }

        this.updateViewsHand();
        this.updateViewsScore();
    }

    /**
     * Ends the game and determines the winner based on total score.
     * Displays the winner and disables all game interactions in the views.
     */
    public void endGame() {
        Player winner = players.getFirst();
        for (Player player : players) {
            if (winner.getScore() < player.getScore()) {
                winner = player;
            }
        }

        JOptionPane.showMessageDialog(null, winner.getName() + " is the Winner!!!");
        for (ScrabbleView view : views) {
            view.endGame();
        }
    }

    /**
     * @return The player whose turn it currently is.
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    /**
     * Moves to the next player's turn in a round-robin fashion.
     */
    public void nextTurn(boolean exchange) {
        if (exchange) {
            if (tileBag.isEmpty()) {
                endPasses += 1;
                if (endPasses == players.size()) {
                    this.endGame();
                }
            } else {
                // Return all tiles to the bag and draw new ones
                while (!this.getCurrentPlayer().getHand().isEmpty()) {
                    tileBag.addTile(this.getCurrentPlayer().removeTile());
                }
                tileBag.shuffle();
                this.getCurrentPlayer().addTile(tileBag);
            }
        } else {
            endPasses = 0;
        }

        currentPlayer = (currentPlayer + 1) % players.size();
        this.updateViewsTopText(this.getCurrentPlayer().getName() + "'s turn.");
        this.updateViewsHand();
        if (getCurrentPlayer() instanceof AIPlayer ai) {
            Move move = ai.getBestMove(dictionary, board, firstTurn);
            if (move != null) placeAIMove(move, firstTurn);
            else nextTurn(true);
        }
    }

    /**
     * Updates all registered views with new text for the top message area.
     */
    public void updateViewsTopText(String newText) {
        for (ScrabbleView view : views) {
            view.updateTopText(newText);
        }
    }

    /**
     * Updates the board display in all views.
     */
    public void updateBoard(boolean validated) {
        for (ScrabbleView view : views) {
            view.updateBoard(placedTiles, validated);
        }
    }

    /**
     * Updates all views with the current player's hand.
     */
    public void updateViewsHand() {
        Player player = this.getCurrentPlayer();
        for (ScrabbleView view : views) {
            view.updateHand(player.getHand());
        }
    }

    /**
     * Disables the "first move" mode in all views after the first valid play.
     */
    public void disableViewsFirstMove() {
        for (ScrabbleView view : views) {
            view.disableFirstMove();
        }
    }


    /**
     * Removes any placed tiles from the board and returns them
     * to the current player's hand, updating the views.
     */
    public void removeViewsPlacedTiles() {
        Player player = this.getCurrentPlayer();
        for (Tile tile : placedTiles) {
            if (tile.getScore() == 0) tile.setLetter(' ');
            player.addTile(tile);
            board.removeTile(tile.getX(), tile.getY());
        }
        placedTiles.clear();
        for (ScrabbleView view : views) {
            view.removePlacedTiles();
        }
    }

    /**
     * Updates the score display in all registered views.
     */
    public void updateViewsScore() {
        StringBuilder scoreText = new StringBuilder();
        for (Player player : players) {
            scoreText.append(player.getName()).append(": ").append(player.getScore()).append("\n");
        }

        for (ScrabbleView view : views) {
            view.updateScore(scoreText.toString(), tileBag.size());
        }
    }

    public void importCustomBoard() {
        board.importCustomBoard();
        this.removeViewsPlacedTiles();
    }

    /**
     * Selects a tile from the player's hand to be placed on the board.
     *
     * @param c the letter of the tile to select
     */
    public void selectTile(char c) {
        this.selectedTile = this.getCurrentPlayer().removeTileByLetter(c);
    }

    /**
     * Attempts to place the currently selected tile on the board.
     *
     * @param x the row index
     * @param y the column index
     * @return true if placement was successful; false otherwise
     */
    public boolean placeTile(int x, int y) {
        if (this.selectedTile == null) {
            this.updateViewsTopText("Select a tile first!");
        } else {
            if (this.selectedTile.getScore() == 0) {
                String input = JOptionPane.showInputDialog("Enter a letter for the blank tile: ");

                if (input != null && !input.trim().isEmpty() && Character.isLetter(input.trim().charAt(0))) {
                    this.selectedTile.setLetter(input.trim().charAt(0));
                } else {
                    JOptionPane.showMessageDialog(null, "ERROR! Please enter a letter for the blank tile!");
                    return false;
                }
            }
            if (board.placeTile(x, y, selectedTile)) {
                this.selectedTile.setCoords(x, y);
                placedTiles.add(selectedTile);
                this.updateBoard(false);
                this.updateViewsTopText(this.getCurrentPlayer().getName() + " placed " + this.selectedTile.getLetter() + " at (" + x + "," + y + ").");
                return true;
            } else {
                if (this.selectedTile.getScore() == 0) {
                    this.selectedTile.setLetter(' ');
                }
                JOptionPane.showMessageDialog(null, "ERROR! Invalid move. Position is either already occupied, or out of bounds.");
            }
        }
        return false;
    }

    /**
     * New helper method that creates a list of all tiles used to create a word.
     *
     * @param startTile    The tile to start searching from (one of the placed tiles).
     * @param isHorizontal The direction to scan (true for horizontal, false for vertical)
     * @return A list of all tiles forming the completed word in the direction.
     */
    public static ArrayList<Tile> getWordTiles(Board board, Tile startTile, boolean isHorizontal) {
        ArrayList<Tile> wordTiles = new ArrayList<>();
        int row = startTile.getX();
        int col = startTile.getY();

        //Scan backwards (left or up) to find the start of the word
        if (isHorizontal) {
            while (col >= 0 && board.getTile(row, col) != null) col--;
            col++; //Move back to the first letter

        }
        else {
            while (row >= 0 && board.getTile(row, col) != null) row--;
            row++; //Move back to the first letter
        }

        //Scan forwards (right or down) to get all tiles in the word
        if (isHorizontal) {
            while (col < Board.SIZE && board.getTile(row, col) != null) {
                wordTiles.add(board.getTile(row, col));
                col++;
            }
        }
        else {
            while (row < Board.SIZE && board.getTile(row, col) != null) {
                wordTiles.add(board.getTile(row, col));
                row++;
            }
        }

        return wordTiles;
    }

    /**
     * Helper method to convert tiles forming a word into a string.
     *
     * @param tiles The list of tiles forming a word.
     * @return A string version of the word being created.
     */
    private static String tilesToString(List<Tile> tiles) {
        StringBuilder sb = new StringBuilder();
        for (Tile tile : tiles) {
            sb.append(tile.getLetter());
        }

        return sb.toString();
    }

    /**
     * Analyzes a move (list of tiles placed on the board) to see if it is valid
     *
     * @param board        The current board
     * @param dictionary   The dictionary containing all the words
     * @param tilesToCheck The list of tiles that are to be placed
     * @param firstTurn    Checks if it is the firstTurn or not
     * @return The score if valid, throws IllegalArgumentException if invalid.
     */
    public static int analyzeMove(Board board, Dictionary dictionary, List<Tile> tilesToCheck, boolean firstTurn) throws IllegalArgumentException {
        if (tilesToCheck.isEmpty()) throw new IllegalArgumentException("ERROR! You have not placed any tiles.");

        boolean sameRow = true;
        boolean sameCol = true;

        //Check if all tiles are on the same row or same column
        for (int i = 1; i < tilesToCheck.size(); i++) {
            if (tilesToCheck.get(i).getX() != tilesToCheck.get(i - 1).getX()) sameCol = false;
            if (tilesToCheck.get(i).getY() != tilesToCheck.get(i - 1).getY()) sameRow = false;
        }

        if (!sameRow && !sameCol) throw new  IllegalArgumentException("ERROR! All tiles must be placed on the same row or column.");

        if (tilesToCheck.size() > 1) {
            List<Tile> sorted = new ArrayList<>(tilesToCheck);
            int start, end, otherCoord;
            if (!sameRow) { //Vertical word
                sorted.sort(Comparator.comparing(Tile::getY)); //Sort by column
                start = sorted.getFirst().getY();
                end = sorted.getLast().getY();
                otherCoord = sorted.getFirst().getX(); //Row is constant
            }
            else { //Horizontal word
                sorted.sort(Comparator.comparing(Tile::getX)); //Sort by row
                start = sorted.getFirst().getX();
                end = sorted.getLast().getX();
                otherCoord = sorted.getFirst().getY(); //Column is constant
            }

            //Check for empty spaces between the start and end of the placed tiles
            if (!board.haveEmptySpace(start, end, otherCoord, !sameRow)) throw new IllegalArgumentException("ERROR! The placed tiles must all be used to form one word.");
        }

        //Ensure first word crosses the center tile
        if (firstTurn && board.getTile(Board.CENTER, Board.CENTER) == null) throw new IllegalArgumentException("ERROR! The first word must pass through the center.");

        int totalScore = 0;
        ArrayList<ArrayList<Tile>> allNewWords = new ArrayList<>();

        //Determine orientation for main word
        ArrayList<Tile> mainWordTiles = getWordTiles(board, tilesToCheck.getFirst(), sameCol);
        allNewWords.add(mainWordTiles);
        ArrayList<Tile> allScoredTiles = new ArrayList<>();
        if (mainWordTiles.size() > 1) allScoredTiles.addAll(mainWordTiles);

        //Find all cross words by looping and checking other directions
        for (Tile tile : tilesToCheck) {
            //Check perpendicular direction for cross-words
            ArrayList<Tile> crossWordTiles = getWordTiles(board, tile, !sameCol); //Check other direction
            if (crossWordTiles.size() > 1) {
                allNewWords.add(crossWordTiles);
                allScoredTiles.addAll(crossWordTiles);
            }
        }

        if (!allScoredTiles.containsAll(tilesToCheck)) throw new IllegalArgumentException("ERROR! All tiles must connect to form valid words (no gaps).");

        if (!firstTurn) {
            boolean connects = false;
            for (Tile tile : allScoredTiles) {
                if (!tilesToCheck.contains(tile)) {
                    connects = true;
                    break;
                }
            }
            if (!connects) throw new IllegalArgumentException("ERROR! The move must connect to an existing tile.");
        }

        //If no new words were created (placed a tile without touching anything)
        if (allNewWords.isEmpty()) throw new IllegalArgumentException("No words formed.");

        //Dictionary validation
        for (ArrayList<Tile> currentWord : allNewWords) {
            String word = tilesToString(currentWord);
            if (!dictionary.isValidWord(word)) throw new IllegalArgumentException("ERROR! " + word + " is not a valid word.");
        }

        //Calculate the score
        ArrayList<Tile> modifiedTiles = new ArrayList<Tile>(tilesToCheck);
        for (Tile tile : tilesToCheck) {
            switch (Board.premiumTiles[tile.getX()][tile.getY()]) {
                case DL:    //If tile is on Double Letter space
                    tile.setScore(tile.getScore() * 2);
                    break;
                case TL:    //If tile is on Triple Letter space
                    tile.setScore(tile.getScore() * 3);
                    break;
                case DW:    //If tile is on Double Word space
                    tile.setScore(tile.getScore() * 2);
                    for (ArrayList<Tile> currentWord : allNewWords) {
                        if (currentWord.contains(tile)) {
                            for (Tile innerTile : currentWord) {
                                if (!tile.equals(innerTile)) {
                                    allScoredTiles.add(innerTile);
                                } 
                            }
                        }
                    }
                    break;
                case TW:    //If tile is on Triple Word space
                    tile.setScore(tile.getScore() * 3);
                    for (ArrayList<Tile> currentWord : allNewWords) {
                        if (currentWord.contains(tile)) {
                            for (Tile innerTile : currentWord) {
                                if (!tile.equals(innerTile)) {
                                    allScoredTiles.add(innerTile);
                                    allScoredTiles.add(innerTile);
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        for (Tile tile : allScoredTiles) {
            totalScore += tile.getScore();
        }

        //Restore original score values of modified tiles
        for (Tile tile : modifiedTiles) {
            if (tile.getScore() != 0) {
                tile.setScore(ScrabbleLetters.get(tile.getLetter()).getScore());
            }
        }

        return totalScore;
    }

    /**
     * Updates the score of the player if their move is valid and adds tiles to their hand.
     * 
     * @param firstTurn     Checks if it is the firstTurn or not
     * @return  whether the move was valid or not.
     */
    public boolean validateMove(boolean firstTurn) {
        try {
            int score = analyzeMove(this.board, this.dictionary, placedTiles, firstTurn);

            //If valid (no exception), make move official
            getCurrentPlayer().addScore(score);

            //Add tiles to current players hand. 
            if (!tileBag.isEmpty()) {
                getCurrentPlayer().addTile(this.tileBag);
                //Check if TileBag is empty.
                if (tileBag.isEmpty()) {
                    updateViewsTopText("Tile bag is now empty!");
                    for (ScrabbleView view : views) view.exchangeToPass();
                }
            }

            if (this.firstTurn) this.firstTurn = false;
            updateViewsScore();
            updateBoard(true);
            placedTiles.clear();
            disableViewsFirstMove();
            return true;
        }
        catch (IllegalArgumentException e) {
            //Send message as to why their move was illegal
            updateViewsTopText(e.getMessage());
            return false;
        }
    }

    /**
     * Performs the logic for an AI to make a move
     * 
     * @param move          The AI's move
     * @param firstTurn     Checks if it is the firstTurn or not
    */
    public void placeAIMove(Move move, boolean firstTurn) {
        int row = move.startRow();
        int col = move.startCol();

        //Place every tile on the board
        for (int i = 0; i < move.word().length(); i++) {
            char letter = move.word().charAt(i);

            if (board.getTile(row, col) == null) {
                Tile tile = getCurrentPlayer().removeTileByLetter(letter);
                if (tile == null) {
                    tile = getCurrentPlayer().removeTileByLetter(' ');
                    if (tile != null) {
                        tile.setLetter(letter);
                    }
                }
                if (tile != null) {
                    tile.setCoords(row, col);
                    board.placeTile(row, col, tile);
                    placedTiles.add(tile);
                }
            }
            if (move.isHorizontal()) col++;
            else row++;
        }

        //Update the board on the views
        this.updateBoard(false);

        if (this.validateMove(firstTurn)) this.nextTurn(false);
        else {
            this.removeViewsPlacedTiles();
            this.nextTurn(true);
        }
    }

    public void saveGame(File file) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(this);
        out.close();
    }

    public static Game loadGame(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        Game loadedGame = (Game) in.readObject();
        in.close();

        loadedGame.views = new ArrayList<>();
        return loadedGame;
    }
}
