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
                    //Call end game function
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
     * Validates the tiles placed during the turn to ensure the move follows Scrabble rules.
     *
     * @param firstTurn Whether this is the first move of the game.
     * @return true if the move is valid, false otherwise.
     */
    public boolean ValidateMove(boolean firstTurn) {
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

        int start;
        int end;
        int otherCoord;


        // Determine word direction and boundaries
        if (sameRow) {
            placedTiles.sort(Comparator.comparingInt(Tile::getX));
            start = placedTiles.getFirst().getX();
            end = placedTiles.getLast().getX();
            otherCoord = placedTiles.getFirst().getY();
        }
        else {
            placedTiles.sort(Comparator.comparingInt(Tile::getY));
            start = placedTiles.getFirst().getY();
            end = placedTiles.getLast().getY();
            otherCoord = placedTiles.getFirst().getX();
        }


        // Ensure placed tiles form a continuous word
        if (!board.haveEmptySpace(start, end, otherCoord, sameRow)) {
            //System.out.println("Error! The placed tiles must all be used to form one word.");
            this.updateViewsTopText("Error! The placed tiles must all be used to form one word.");
            return false;
        }


        // Ensure first word crosses the center tile
        if (firstTurn && board.getTile(Board.CENTER, Board.CENTER) == null) {
            //System.out.println("ERROR! The first word must pass through the center.");
            this.updateViewsTopText("ERROR! The first word must pass through the center.");
            return false;
        }

        // Validate all formed words using the dictionary
        for (String word: board.getPlacedWords()) {
            if (!(dictionary.isValidWord(word))) return false; 
        }

        // Calculate and add score for placed tiles
        int score = 0;
        for (Tile tile : placedTiles) {
            score += tile.getScore();
        }
        this.getCurrentPlayer().addScore(score);
        this.getCurrentPlayer().addTile(this.tileBag);
        
        this.updateBoard(true);
        placedTiles.clear();
        if (firstTurn) firstTurn = false;
        this.disableViewsFirstMove();
        
        return true;
    }
}
