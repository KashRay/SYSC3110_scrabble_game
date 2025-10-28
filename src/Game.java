import java.util.*;

public class Game {
    private final Board board;
    private final TileBag tileBag;
    private final Dictionary dictionary;
    private final List<Player> players;
    private int currentPlayer;
    private static ArrayList<Tile> placedTiles;
    public boolean didExchangeOrPass;

    public Game() {
        board = new Board();
        tileBag = new TileBag();
        dictionary = new Dictionary();
        dictionary.loadFromFile("wordlist.txt");
        players = new ArrayList<>();
        currentPlayer = 0;
        placedTiles = new ArrayList<>();
        didExchangeOrPass = false;
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
        for (Player player : players) {
            player.addTile(tileBag);
        }
        System.out.println("Game started with " + players.size() + " players!");

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
    public void nextTurn() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }

    /**
     * Displays the current board state and each player's hand and score.
     */
    public void displayBoard() {
        System.out.println("Current board:");
        System.out.println(board.toString());
        for (Player player : players) {
            System.out.println(player.toString());
        }
    }

    /**
     * Handles user input for making a move.
     * Players can place tiles, exchange tiles, pass their turn
     *
     * @param player The player currently making a move.
     */
    public void makeMove(Player player) {
        Scanner scanner = new Scanner(System.in);
        String move = "";

        System.out.println("Player " + player.getName() + " is making a move! To make a move, enter the command in the format <LETTER> <ROW> <COLUMN> (e.g.: 'A 7 8').");
        System.out.println("If you wish to exchange your letters, type 'exchange'.");
        System.out.println("If you wish to pass your turn, type 'pass'.");
        System.out.println("(When you are done making a move, type 'done'.");


        // Keep reading input until player finishes, exchanges, or passes
        while (!move.equals("done") && !move.equals("exchange") && !move.equals("pass")) {
            System.out.print("Enter a command: ");
            move = scanner.nextLine().trim();

            switch (move) {
                case "done" -> {
                    // Ensure the player has placed at least one tile before finishing
                    if (placedTiles.isEmpty()) {
                        System.out.println("ERROR! You have to place at least one letter.");
                        move = "";
                    }
                    continue;
                }
                case "pass" -> {
                    System.out.println(player.getName() + " skipped their turn.");
                    didExchangeOrPass = true;
                    continue;
                }
                case "exchange" -> {
                    // Return all tiles to the bag and draw new ones
                    while (!player.getHand().isEmpty()) {
                        tileBag.addTile(player.removeTile());
                    }
                    tileBag.shuffle();
                    player.addTile(tileBag);
                    didExchangeOrPass = true;
                    continue;
                }
            }

            String[] parts = move.split(" ");

            if (parts.length != 3) {
                System.out.println("ERROR! Please enter a valid move. Format should be <LETTER> <ROW> <COLUMN>.");
                continue;
            }

            char letter = Character.toUpperCase(parts[0].charAt(0));
            int row, col;

            // Parse coordinates
            try {
                row = Integer.parseInt(parts[1]);
                col = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                System.out.println("ERROR! Invalid coordinates. Must be integers.");
                continue;
            }


            // Find the corresponding tile in the player's hand
            Tile selectedTile = null;
            for (Tile tile : player.getHand()) {
                if (tile.getLetter() == letter) {
                    selectedTile = tile;
                    break;
                }
            }

            // Error if tile not found
            if (selectedTile == null) {
                System.out.println("ERROR! You don't have the letter '" + letter + "' in your hand.");
                continue;
            }

            // Error if tile not found
            if (board.placeTile(row, col, selectedTile)) {
                selectedTile.setCoords(row, col);
                placedTiles.add(selectedTile);
                System.out.println(board);
                System.out.println(player.getName() + " placed " + letter + " at (" +  row + "," + col + ").");
            }
            else {
                System.out.println("ERROR! Invalid move. Position is either already occupied, or out of bounds.");
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
            System.out.println("ERROR! All tiles must be placed on the same row or column");
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
            System.out.println("Error! The placed tiles must all be used to form one word.");
            return false;
        }


        // Ensure first word crosses the center tile
        if (firstTurn && board.getTile(Board.CENTER, Board.CENTER) == null) {
            System.out.println("ERROR! The first word must pass through the center.");
            return false;
        }


        // Validate all formed words using the dictionary
        for (String word: board.getPlacedWords()) {
            if (!(dictionary.isValidWord(word))) return false; 
        }

        return true;
    }

    /**
     * Main entry point for running the game.
     * Handles setup, player turns, and the main game loop.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();
        Board board = game.getBoard();
        TileBag tilebag = game.getTileBag();
        boolean playable = true;
        boolean firstTurn = true;
        int numPlayers;

        System.out.println("Welcome to SCRABBLE!");
        while (true) {

            // Prompt user for number of players
            System.out.print("Please enter the number of players (2-4): ");
            numPlayers = Integer.parseInt(scanner.nextLine());
            if  (numPlayers < 2 || numPlayers > 4) {
                System.out.println("ERROR! Please enter a number between 2 and 4.");
                continue;
            }
            break;
        }

        // Gather player names
        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter a name for Player " + i + ": ");
            String name = scanner.nextLine();
            game.addPlayer(name);
        }

        game.startGame();

        // Main game loop
        while (playable) {
            game.displayBoard();
            while (true) {
                game.makeMove(game.getCurrentPlayer());
                // Move to next player if they passed or exchanged
                if (game.didExchangeOrPass){
                    game.didExchangeOrPass = false;
                    game.nextTurn();
                    break;
                }
                else if (game.ValidateMove(firstTurn)) {
                    // Calculate and add score for placed tiles
                    int score = 0;
                    for (Tile tile : placedTiles) {
                        score += tile.getScore();
                    }
                    game.getCurrentPlayer().addScore(score);
                    game.getCurrentPlayer().addTile(tilebag);
                    game.nextTurn();
                    placedTiles.clear();
                    if (firstTurn) firstTurn = false;
                    break;
                }
                else {
                    // Invalid move: return tiles to player's hand
                    for (Tile tile : placedTiles) {
                        game.getCurrentPlayer().addTile(board.removeTile(tile.getX(), tile.getY()));
                    }
                    placedTiles.clear();
                }
            }

            // End the game if no tiles remain
            if (tilebag.isEmpty()) {
                playable = false;
            }
        }
    }
}
