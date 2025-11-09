import java.util.*;

public class Game {
    private final Board board;
    private final TileBag tileBag;
    private final Dictionary dictionary;
    private final List<Player> players;
    private int currentPlayer;
    private static ArrayList<Tile> placedTiles;
    public boolean didExchangeOrPass;
    private final ArrayList<ScrabbleView> views;

    public Game() {
        board = new Board();
        tileBag = new TileBag();
        dictionary = new Dictionary();
        dictionary.loadFromFile("wordlist.txt");
        players = new ArrayList<Player>();
        currentPlayer = 0;
        placedTiles = new ArrayList<Tile>();
        didExchangeOrPass = false;
        views = new ArrayList<ScrabbleView>();
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
     * New helper method to that creates a list of all tiles used to create a word.
     * @param startTile The tile to start searching from (one of the placed tiles).
     * @param isHorizontal The direction to scan (true for horizontal, false for vertical)
     * @return A list of all tiles forming the completed word in the direction
     */
    public ArrayList<Tile> getWordTiles(Tile startTile, boolean isHorizontal) {
        ArrayList<Tile> wordTiles = new ArrayList<>();
        int row = startTile.getX();
        int col = startTile.getY();

        //Scan "backwards" (left or up) to find the start of the word
        if (isHorizontal) {
            while (col >= 0 && board.getTile(row, col) != null) {
                col--;
            }
            col++; //Move back to the first tile of the word
        } else {
            while (row >= 0 && board.getTile(row, col) != null) {
                row--;
            }
            row++; //Move back to the first tile of the word
        }

        //Scan "forwards" (right or down) to get all tiles in the word
        if (isHorizontal) {
            while (col < Board.SIZE && board.getTile(row, col) != null) {
                wordTiles.add(board.getTile(row, col));
                col++;
            }
        } else {
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

            //Remove the tiles from the player's hand
            Tile selectedTile = player.removeTileByLetter(letter);

            //Give error if tile is not found in hand
            if (selectedTile == null) {
                System.out.println("ERROR! You don't have the letter '" + letter + "' in your hand.");
                continue;
            }

            //Try to place the removed tile on the board
            if (board.placeTile(row, col, selectedTile)) {
                selectedTile.setCoords(row, col);
                placedTiles.add(selectedTile);
                System.out.println(board);
                System.out.println(player.getName() + " placed '" + letter + "' at (" + row + "," + col + ").");
            }
            else {
                //If placement fails, add the tile back to the player's hand
                player.addTile(selectedTile);
                System.out.println("ERROR! Invalid move. Position is either already occupied, or out of bounds.");
            }
        }
    }

    /**
     * Validates the current move and calculates the score to ensure the move follows Scrabble rules.
     *
     * @param firstTurn True if this is the first move of the game.
     * @return  The total score of the move if valid, or -1 if invalid.
     */
    public int ValidateAndScoreMove(boolean firstTurn) {
        if (placedTiles.isEmpty()) return -1; //No tiles placed

        boolean sameRow = true;
        boolean sameCol = true;

        // Check if all tiles are in the same row or column
        for (int i = 1; i < placedTiles.size(); i++) {
            if (placedTiles.get(i).getX() != placedTiles.get(i-1).getX()) sameCol = false;
            if (placedTiles.get(i).getY() != placedTiles.get(i-1).getY()) sameRow = false;
        }

        if (!sameRow && !sameCol) {
            System.out.println("ERROR! All tiles must be placed on the same row or column");
            return -1;
        }

        if (firstTurn) {
            boolean coversCenter = false;
            for (Tile tile : placedTiles) {
                if (tile.getX() == Board.CENTER && tile.getY() == Board.CENTER) {
                    coversCenter = true;
                    break;
                }
            }

            if (!coversCenter) return -1;
        }

        int totalScore = 0;
        Set<Tile> allScoredTiles = new HashSet<>();
        ArrayList<String> allNewWords = new ArrayList<>();

        //Find the "main" word (horizontal or vertical)
        ArrayList<Tile> mainWordTiles = getWordTiles(placedTiles.getFirst(), sameRow);
        if (mainWordTiles.size() > 1) {
            allNewWords.add(tilesToString(mainWordTiles));
            allScoredTiles.addAll(mainWordTiles);
        }

        //Find all "cross" words
        if (placedTiles.size() > 1 || mainWordTiles.isEmpty()) { //Only look for cross-words if >1 tile or no main word
            for (Tile placedTile : placedTiles) {
                ArrayList<Tile> crossWordTiles = getWordTiles(placedTile, !sameRow); //Check other direction
                if (crossWordTiles.size() > 1) {
                    allNewWords.add(tilesToString(crossWordTiles));
                    allScoredTiles.addAll(crossWordTiles);
                }
            }
        }

        //Check for connection to already existing tile
        if (!firstTurn) {
            boolean connects = false;
            for (Tile tile : allScoredTiles) {
                if (!placedTiles.contains(tile)) {
                    connects = true;
                    break;
                }
            }

            if (!connects) {
                System.out.println("ERROR! Move must connect to an existing tile.");
                return -1;
            }
        }

        if (allNewWords.isEmpty()) return -1; //No valid word formed (e.g. single tile)

        for (String word : allNewWords) {
            if (!dictionary.isValidWord(word)) {
                System.out.println("ERROR! '" + word + "' is not a valid word.");
                return -1;
            }
        }

        //Final score calculation
        for (Tile tile : allScoredTiles) {
            totalScore += tile.getScore();
        }

        return totalScore;
    }

    /**
     * Main entry point for running the game.
     * Handles setup, player turns, and the main game loop.
     */
    public void play() {
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

                //Validate the move and get the score in one step
                int moveScore = game.ValidateAndScoreMove(firstTurn);

                if (moveScore >= 0) { //A score of 0 or more is valid
                    for (ScrabbleView view : views) {
                        view.updateBoard(placedTiles);
                    }

                    //Add the calculated score
                    game.getCurrentPlayer().addScore(moveScore);
                    game.getCurrentPlayer().addTile(tilebag);
                    game.nextTurn();
                    placedTiles.clear();

                    if (firstTurn) firstTurn = false;
                    break; //Valid move, exit inner loop
                } else {
                    //Invalid move, return tiles to player's hand
                    System.out.println("ERROR! Invalid move. Please try again.");
                    for (Tile tile : placedTiles) {
                        game.getCurrentPlayer().addTile(board.removeTile(tile.getX(), tile.getY()));
                    }
                    placedTiles.clear();
                    //Loop continues to ask for a new move
                }
            }

            // End the game if no tiles remain
            if (tilebag.isEmpty()) {
                playable = false;
            }
        }
    }
}
