import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

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
    private transient Dictionary dictionary;
    private final List<Player> players;
    private int currentPlayer;
    private ArrayList<Tile> placedTiles;
    private transient ArrayList<ScrabbleView> views;
    private Tile selectedTile;
    private int endPasses;
    private boolean firstTurn;
    private static final long serialVersionUID = 1L;
    private static Stack<byte[]> undoStack;
    private static Stack<byte[]> redoStack;

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
        undoStack = new Stack<byte[]>();
        redoStack = new Stack<byte[]>();
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
        //Calculate the winner
        Player winner = players.getFirst();
        for (Player player : players) {
            if (winner.getScore() < player.getScore()) {
                winner = player;
            }
        }

        //Creating string for end screen statistics
        StringBuilder endScreen = new  StringBuilder();
        endScreen.append("GAME OVER!\n");
        endScreen.append(winner.getName()).append(" is the WINNER!\n");
        endScreen.append("--- FINAL STATS ---\n");
        for (Player player : players) {
            endScreen.append("Player: ").append(player.getName()).append("\n");
            endScreen.append("\tTotal Score: ").append(player.getScore()).append("\n");
            endScreen.append("\tTurns Played: ").append(player.getTurnsTaken()).append("\n");
            endScreen.append("\tWords Played:\n");
            if (player.getRecordedMoves().isEmpty()) endScreen.append("\t(None)\n");
            else {
                for (PlayerMove move : player.getRecordedMoves()) {
                    endScreen.append("\t").append(move.toString()).append("\n");
                }
            }
            endScreen.append("\n");
        }

        //Display in a scrollable dialog
        JTextArea textArea = new JTextArea(endScreen.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        JOptionPane.showMessageDialog(null, scrollPane, "Game Results", JOptionPane.INFORMATION_MESSAGE);

        //Disable views
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

    public Board getBoard() { return this.board; }

    public TileBag getTileBag() { return this.tileBag; }

    public Dictionary getDictionary() { return this.dictionary; }

    public List<Player> getPlayers() { return this.players; }

    public int getCurrentPlayerNum() { return this.currentPlayer; }

    public ArrayList<Tile> getPlacedTiles() { return this.placedTiles; }

    public ArrayList<ScrabbleView> getViews() { return this.views; }

    public Tile getSelectedTile() { return this.selectedTile; };

    public int getEndPasses() { return this.endPasses; }
    
    public boolean getFirstTurn() { return this.firstTurn; }

    public Stack<byte[]> getUndoStack() { return undoStack; }

    public Stack<byte[]> getRedoStack() { return redoStack; }

    /**
     * Moves to the next player's turn in a round-robin fashion.
     */
    public void nextTurn(boolean exchange) {
        this.getCurrentPlayer().incrementTurns();

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
            AIMove AIMove = ai.getBestMove(dictionary, board, firstTurn);
            if (AIMove != null) placeAIMove(AIMove, firstTurn);
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
     * Disables the "first mainWord" mode in all views after the first valid play.
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

    /**
     * Updated the undo button in all registered views.
     * 
     * @param toggle  whether the undo button is enabled or disabled.
     */
    public void updateViewsUndo(boolean toggle) {
        for (ScrabbleView view : views) {
            view.toggleUndo(toggle);
        }
    }

    /**
     * Updated the redo button in all registered views.
     * 
     * @param toggle  whether the redo button is enabled or disabled.
     */
    public void updateViewsRedo(boolean toggle) {
        for (ScrabbleView view : views) {
            view.toggleRedo(toggle);
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
                this.selectedTile = null;
                return true;
            } else {
                if (this.selectedTile.getScore() == 0) {
                    this.selectedTile.setLetter(' ');
                }
                JOptionPane.showMessageDialog(null, "ERROR! Invalid mainWord. Position is either already occupied, or out of bounds.");
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
     * Analyzes a mainWord (list of tiles placed on the board) to see if it is valid
     *
     * @param board        The current board
     * @param dictionary   The dictionary containing all the words
     * @param tilesToCheck The list of tiles that are to be placed
     * @param firstTurn    Checks if it is the firstTurn or not
     * @return The score if valid, throws IllegalArgumentException if invalid.
     */
    public static PlayerMove analyzeMove(Board board, Dictionary dictionary, List<Tile> tilesToCheck, boolean firstTurn) throws IllegalArgumentException {
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
            if (!connects) throw new IllegalArgumentException("ERROR! The mainWord must connect to an existing tile.");
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

        return new PlayerMove(totalScore, tilesToString(mainWordTiles));
    }

    /**
     * Updates the score of the player if their mainWord is valid and adds tiles to their hand.
     * 
     * @param firstTurn     Checks if it is the firstTurn or not
     * @return  whether the mainWord was valid or not.
     */
    public boolean validateMove(boolean firstTurn) {
        try {
            PlayerMove move = analyzeMove(this.board, this.dictionary, placedTiles, firstTurn);

            //If valid (no exception), make mainWord official
            getCurrentPlayer().addScore(move.totalScore());
            getCurrentPlayer().addMove(move);

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
            //Send message as to why their mainWord was illegal
            updateViewsTopText(e.getMessage());
            return false;
        }
    }

    /**
     * Performs the logic for an AI to make a mainWord
     * 
     * @param AIMove the AI's mainWord
     * @param firstTurn checks if it is the firstTurn or not
    */
    public void placeAIMove(AIMove AIMove, boolean firstTurn) {
        int row = AIMove.startRow();
        int col = AIMove.startCol();

        //Place every tile on the board
        for (int i = 0; i < AIMove.word().length(); i++) {
            char letter = AIMove.word().charAt(i);

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
            if (AIMove.isHorizontal()) col++;
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

    /**
     * Serializes the current game state and pushes it onto the specified stack.
     * This method is used internally by the undo/redo system to capture snapshots
     * of the game at various points in time. The Game object is fully serialized
     * into a byte array and stored so that it can later be restored via
     * undo() or redo().
     *
     * @param stack the stack onto which the serialized game state should be pushed.
     *              Typically either the undoStack or redoStack.
     * @throws IOException if an I/O error occurs during serialization.
     */
    public void storeState(Stack<byte[]> stack) throws IOException {
        System.out.println("Storing State:");
        if (stack.equals(undoStack)) {
            System.out.println("UndO");
        }
        else {
            System.out.println("Redo");
        }
        System.out.println(stack.size());
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream state = new ObjectOutputStream(byteStream);
        state.writeObject(this);
        stack.push(byteStream.toByteArray());
        System.out.println(stack.size());
    }

    /**
     * Reverts the game to the most recently stored previous state.
     * The current state is first saved onto the redo stack, allowing the user
     * to reapply the undone action using redo(). The most recent
     * serialized state from the undo stack is then deserialized and returned
     * as a new Game instance.
     * After loading, the new game instance reinitializes transient fields such as
     * the list of views and the dictionary.
     *
     * @return a new Game object representing the previous state.
     * @throws IOException if an I/O error occurs during deserialization.
     * @throws ClassNotFoundException if the serialized class definition cannot be found.
     */
    public Game undo() throws IOException, ClassNotFoundException {
        try {
            if (redoStack.isEmpty()) this.updateViewsRedo(true);
            this.storeState(redoStack);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to store in redo stack.");
        }

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(undoStack.pop()));

        if (undoStack.isEmpty()) this.updateViewsUndo(false);
        
        Game loadedGame = (Game) in.readObject();

        loadedGame.views = new ArrayList<>();
        loadedGame.dictionary = new Dictionary();
        loadedGame.dictionary.loadFromFile("src/wordlist.txt");
        return loadedGame;
    }

    /**
     * Reapplies the most recently undone operation by restoring a state stored in
     * the redo stack.
     * The current state is first pushed onto the undo stack, enabling the user to
     * undo the redo if necessary. The method then restores the most recent redo
     * state by deserializing it and returning a new Game instance.
     * After loading, the returned instance resets transient fields such as the
     * dictionary and the list of views.
     *
     * @return a new Game object representing the redone state.
     * @throws IOException if an I/O error occurs during deserialization.
     * @throws ClassNotFoundException if the serialized class definition cannot be found.
     */
    public Game redo() throws IOException, ClassNotFoundException {
        try {
            if (undoStack.isEmpty()) this.updateViewsUndo(true);
            this.storeState(undoStack);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to store in undo stack.");
        }

        ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(redoStack.pop()));
        
        if (redoStack.isEmpty()) this.updateViewsRedo(false);
        
        Game loadedGame = (Game) in.readObject();

        loadedGame.views = new ArrayList<>();
        loadedGame.dictionary = new Dictionary();
        loadedGame.dictionary.loadFromFile("src/wordlist.txt");
        return loadedGame;
    }

    /**
     * Removes all stored undo history from the undo stack.
     * If the stack is not empty, it is cleared and the UI is notified
     * via updateViewsUndo(false) that undo is no longer available.
     */
    public void clearUndoStack() {
        if (!undoStack.isEmpty()) {
            undoStack.clear();
            this.updateViewsUndo(false);
        }
    }

    /**
     * Removes all stored redo history from the redo stack.
     * If the stack is not empty, it is cleared and the UI is notified
     * via updateViewsRedo(false) that redo is no longer available.
     */
    public void clearRedoStack() {
        if (!redoStack.isEmpty()) {
            redoStack.clear();
            this.updateViewsRedo(false);
        }
    }

    /**
     * Saves the current game state to a file using Java serialization.
     * The state is written exactly as stored in memory, allowing it to be
     * restored later through loadGame(File).
     *
     * @param file the file to which the game state will be written.
     * @throws IOException if an error occurs while writing to the file.
     */
    public void saveGame(File file) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(this);
        out.close();
    }

    /**
     * Loads a previously saved game state from the specified file.
     * After the serialized object is deserialized, transient fields such as the
     * list of views and the dictionary are reinitialized to ensure proper runtime
     * behavior.
     *
     * @param file the file containing a previously saved game state.
     * @return a new Game instance reconstructed from the file.
     * @throws IOException if an error occurs while reading the file.
     * @throws ClassNotFoundException if the serialized class cannot be found.
     */
    public static Game loadGame(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        Game loadedGame = (Game) in.readObject();
        in.close();

        loadedGame.views = new ArrayList<>();
        loadedGame.dictionary = new Dictionary();
        loadedGame.dictionary.loadFromFile("src/wordlist.txt");
        return loadedGame;
    }

    /**
     * Compares this game instance with another object for logical equality.
     * Two games are considered equal if all core game components—such as the
     * board, tile bag, dictionary, player list, current turn index, placed tiles,
     * and selected tile—are equivalent. Simple primitive fields such as
     * endPasses and firstTurn must also match exactly.
     *
     * @param o the object to compare with this game.
     * @return true if the objects represent the same game state;
     * false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;

        Game other = (Game) o;

        return Objects.equals(board, other.getBoard()) &&
                Objects.equals(tileBag, other.getTileBag()) &&
                Objects.equals(dictionary, other.getDictionary()) &&
                Objects.equals(players, other.getPlayers()) &&
                currentPlayer == other.getCurrentPlayerNum() &&
                Objects.equals(placedTiles, other.getPlacedTiles()) &&
                Objects.equals(selectedTile, other.getSelectedTile()) &&
                endPasses == other.getEndPasses() &&
                firstTurn == other.getFirstTurn();
    }

}
