import java.util.*;

import javax.swing.JOptionPane;

public class Game {
    private final Board board;
    private final TileBag tileBag;
    private final Dictionary dictionary;
    private final List<Player> players;
    private int currentPlayer;
    private static ArrayList<Tile> placedTiles;
    private ArrayList<ScrabbleView> views;
    private Tile selectedTile;
    private int endPasses;

    public Game() {
        board = new Board();
        tileBag = new TileBag();
        dictionary = new Dictionary();
        dictionary.loadFromFile("wordlist.txt");
        players = new ArrayList<>();
        currentPlayer = 0;
        placedTiles = new ArrayList<Tile>();
        views = new ArrayList<ScrabbleView>();
        selectedTile = null;
        endPasses = 0;
    }

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
     * Starts the game by distributing tiles to each player.
     */
    public void startGame() {
        int numPlayers = 0;

        this.updateViewsTopText("Welcome to SCRABBLE!");
        while (true) {

            try {
                // Prompt user for number of players
                numPlayers = Integer.parseInt(JOptionPane.showInputDialog("Please enter the number of players (2-4):"));
                if  (numPlayers < 2 || numPlayers > 4) {
                    JOptionPane.showMessageDialog(null, "ERROR! Please enter a number between 2 and 4.");
                    continue;
                }
                break;
            }
            catch (Exception e){
                JOptionPane.showMessageDialog(null, "ERROR! Please enter an Integer.");
            }
        }

        // Gather player names
        for (int i = 1; i <= numPlayers; i++) {
            String name = JOptionPane.showInputDialog("Enter a name for Player " + i + ": ");
            this.addPlayer(name);
        }
        
        for (Player player : players) {
            player.addTile(tileBag);
        }
        System.out.println("Game started with " + players.size() + " players!");

        this.updateViewsHand();
        this.updateViewsScore();
    }

    public void endGame() {
        Player winner = null;
        for (Player player : players) {
            if (winner == null) {
                winner = player;
            }
            else {
                if (winner.getScore() < player.getScore()) {
                    winner = player;
                }
            }
        }

        JOptionPane.showMessageDialog(null, winner.getName() + " is the Winner!!!");
        for (ScrabbleView view : views) {
            view.endGame();
        }
    }

    /**
     * @return The current game board.
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * @return The tile bag shared among players.
     */
    public TileBag getTileBag() {
        return this.tileBag;
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
            }
            else {
                // Return all tiles to the bag and draw new ones
                while (!this.getCurrentPlayer().getHand().isEmpty()) {
                    tileBag.addTile(this.getCurrentPlayer().removeTile());
                }
                tileBag.shuffle();
                this.getCurrentPlayer().addTile(tileBag);
            }
        }
        else {
            endPasses = 0;
        }
        
        currentPlayer = (currentPlayer + 1) % players.size();
        this.updateViewsTopText(this.getCurrentPlayer().getName() + "'s turn.");
        this.updateViewsHand();

        for (Tile tile : this.getCurrentPlayer().getHand()) {
            System.out.print("" + tile.getLetter());
        }
        System.out.println();
    }

    public void updateViewsTopText(String newText) {
        for (ScrabbleView view : views) {
            view.updateTopText(newText);
        }
    }

    public void updateBoard(boolean validated) {
        for (ScrabbleView view : views) {
            view.updateBoard(placedTiles, validated);
        }
    }

    public void updateViewsHand() {
        Player player = this.getCurrentPlayer();
        for (ScrabbleView view : views) {
            view.updateHand(player.getHand());
        }
    }

    public void disableViewsFirstMove() {
        for (ScrabbleView view : views) {
            view.disableFirstMove();
        }
    }

    public void removeViewsPlacedTiles() {
        Player player = this.getCurrentPlayer();
        for (Tile tile : placedTiles) {
            player.addTile(tile);
            board.removeTile(tile.getX(), tile.getY());
        }
        placedTiles.clear();
        for (ScrabbleView view : views) {
            view.removePlacedTiles();
        }
    }

    public void updateViewsScore() {
        String scoreText = "";
        for (Player player : players) {
            scoreText += player.getName() + ": " + Integer.toString(player.getScore()) + "\n";
        }

        for (ScrabbleView view : views) {
            view.updateScore(scoreText);
        }
    }

    public void selectTile(char c) {
        this.selectedTile = this.getCurrentPlayer().removeTileByLetter(c);
    }

    public void placeTile(int x, int y) {
        if (this.selectedTile == null) {
            this.updateViewsTopText("Select a tile first!");
        }
        else {
            if (board.placeTile(x, y, selectedTile)) {
                this.selectedTile.setCoords(x, y);
                placedTiles.add(selectedTile);
                this.updateBoard(false);
                JOptionPane.showMessageDialog(null, this.getCurrentPlayer().getName() + " placed " + this.selectedTile.getLetter() + " at (" +  x + "," + y + ").");
            }
            else {
                JOptionPane.showMessageDialog(null, "ERROR! Invalid move. Position is either already occupied, or out of bounds.");
            }
        }
    }

    /**
     * New helper method that creates a list of all tiles used to create a word.
     * @param startTile The tile to start searching from (one of the placed tiles).
     * @param isHorizontal The direction to scan (true for horizontal, false for vertical)
     * @return A list of all tiles forming the completed word in the direction.
     */
    public ArrayList<Tile> getWordTiles(Tile startTile, boolean isHorizontal) {
        ArrayList<Tile> wordTiles = new ArrayList<>();
        int row = startTile.getX();
        int col = startTile.getY();

        //Scan backwards (left or up) to find the start of the word
        if (isHorizontal) {
            while (col >= 0 && board.getTile(row, col) != null) {
                col--;
            }
            col++; //Move back to the first letter
        }
        else {
            while (row >= 0 && board.getTile(row, col) != null) {
                row--;
            }
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
     * @param tiles The list of tiles forming a word.
     * @return A string version of the word being created.
     */
    private String tilesToString(List<Tile> tiles) {
        StringBuilder sb = new StringBuilder();
        for (Tile tile : tiles) {
            sb.append(tile.getLetter());
        }

        return sb.toString();
    }

    /**
     * Validates the tiles placed during the turn to ensure the move follows Scrabble rules.
     * If valid, it calculates teh score, adds it to the player, and returns true.
     * If invalid, it updates teh view with an error message and returns false.
     *
     * @param firstTurn Whether this is the first move of the game.
     * @return true if the move is valid, false otherwise.
     */
    public boolean ValidateMove(boolean firstTurn) {
        if (placedTiles.isEmpty()) {
            this.updateViewsTopText("ERROR! You have not placed any tiles.");
            return false;
        }

        boolean sameRow = true;
        boolean sameCol = true;


        // Check if all tiles are in the same row or column
        for (int i = 1; i < placedTiles.size(); i++) {
            if (placedTiles.get(i).getX() != placedTiles.get(i-1).getX()) sameCol = false;
            if (placedTiles.get(i).getY() != placedTiles.get(i-1).getY()) sameRow = false;
        }

        if (!sameRow && !sameCol) {
            //System.out.println("ERROR! All tiles must be placed on the same row or column");
            this.updateViewsTopText("ERROR! All tiles must be placed on the same row or column");
            return false;
        }


        if (placedTiles.size() > 1) {
            int start, end, otherCoord;
            if (sameRow) {
                placedTiles.sort(Comparator.comparingInt(Tile::getY)); //Sort by column
                start = placedTiles.getFirst().getY();
                end = placedTiles.getLast().getY();
                otherCoord = placedTiles.getFirst().getX(); //Row is constant
            } else {
                placedTiles.sort(Comparator.comparingInt(Tile::getX)); //Sort by row
                start = placedTiles.getFirst().getX();
                end = placedTiles.getLast().getX();
                otherCoord = placedTiles.getFirst().getY(); //Column is constant
            }

            //Check for empty spaces between the start and end of the placed tiles
            if (!board.haveEmptySpace(start, end, otherCoord, sameRow)) {
                this.updateViewsTopText("Error! The placed tiles must all be used to form one word.");
                return false;
            }
        }

        // Ensure first word crosses the center tile
        if (firstTurn && board.getTile(Board.CENTER, Board.CENTER) == null) {
            this.updateViewsTopText("ERROR! The first word must pass through the center.");
            return false;
        }

        int totalScore = 0;
        Set<Tile> allScoredTiles = new HashSet<>(); //Use a Set to avoid double-scoring tiles
        ArrayList<String> allNewWords = new ArrayList<>();

        //Find the main word (horizontal or vertical
        ArrayList<Tile> mainWordTiles = getWordTiles(placedTiles.getFirst(), sameRow);
        if (mainWordTiles.size() > 1) {
            allNewWords.add(tilesToString(mainWordTiles));
            allScoredTiles.addAll(mainWordTiles);
        }

        //Find all cross words by looping and checking other directions
        for (Tile placedTile : placedTiles) {
            ArrayList<Tile> crossWordTiles = getWordTiles(placedTile, !sameRow); //Check other direction
            if (crossWordTiles.size() > 1) {
                allNewWords.add(tilesToString(crossWordTiles));
                allScoredTiles.addAll(crossWordTiles);
            }
        }

        if (!firstTurn) {
            boolean connects = false;
            for (Tile tile : allScoredTiles) {
                if (!placedTiles.contains(tile)) {
                    connects = true;
                    break;
                }
            }
            if (!connects) {
                this.updateViewsTopText("ERROR! Move must connect to an existing tile.");
                return false;
            }
        }

        //If no new words were created (placed a tile without touching anything)
        if (allNewWords.isEmpty()) {
            this.updateViewsTopText("ERROR! Your move did not form any new words.");
            return false;
        }

        //Dictionary validation
        for (String word : allNewWords) {
            if (!dictionary.isValidWord(word)) {
                this.updateViewsTopText("ERROR! '" + word + "' is not a valid word.");
                return false;
            }
        }

        for (Tile tile : allScoredTiles) {
            totalScore += tile.getScore();
        }



        this.getCurrentPlayer().addScore(totalScore);
        //this.updateViewsTopText(this.getCurrentPlayer().getName() + " has scored " + totalScore + " pts.");
        this.updateViewsScore();

        if (!tileBag.isEmpty()) {
            this.getCurrentPlayer().addTile(this.tileBag);
            if (tileBag.isEmpty()) {
                this.updateViewsTopText("Tilebag is now empty!");
                for (ScrabbleView view : views) {
                    view.exchangeToPass();
                }
            }
        }
        this.updateBoard(true);
        placedTiles.clear();
        if (firstTurn) firstTurn = false;
        this.disableViewsFirstMove();

        return true;
    }
}
