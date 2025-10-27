import java.util.*;

public class Game {
    private final Board board;
    private final TileBag tileBag;
    private final Dictionary dictionary;
    private final List<Player> players;
    private int currentPlayer;
    private static ArrayList<Tile> placedTiles;
    public boolean didExchange;

    public Game() {
        board = new Board();
        tileBag = new TileBag();
        dictionary = new Dictionary();
        dictionary.loadFromFile("wordlist.txt");
        players = new ArrayList<>();
        currentPlayer = 0;
        placedTiles = new ArrayList<>();
        didExchange = false;
    }

    public void addPlayer(String name) {
        players.add(new Player(name));
    }

    public void startGame() {
        for (Player player : players) {
            player.addTile(tileBag);
        }
        System.out.println("Game started with " + players.size() + " players!");

    }

    public Board getBoard() {
        return this.board;
    }

    public TileBag getTileBag() {
        return this.tileBag;
    }

    public Player getCurrentPlayer() {
        return players.get(currentPlayer);
    }

    public void nextTurn() {
        currentPlayer = (currentPlayer + 1) % players.size();
    }

    public void displayBoard() {
        System.out.println("Current board:");
        System.out.println(board.toString());
        for (Player player : players) {
            System.out.println(player.toString());
        }
    }

    public void makeMove(Player player) {
        Scanner scanner = new Scanner(System.in);
        String move = "";

        System.out.println("Player " + player.getName() + " is making a move! To make a move, enter the command in the format <LETTER> <ROW> <COLUMN> (e.g.: 'A 7 8').\nIf you wish to exchange your letters, type 'exchange'.\nWhen you are done making a move, type 'done'.");
        while (!move.equals("done") && ! move.equals("exchange")) {
            move = scanner.nextLine().trim();

            if (move.equals("done")) {
                if (placedTiles.isEmpty()) {
                    System.out.println("You have to place at least one letter.");
                    move = "";
                }    
                continue;
            }

            if (move.equals("exchange")) {
                while (!player.getHand().isEmpty()) {
                    tileBag.addTile(player.removeTile());
                }
                tileBag.shuffle();
                player.addTile(tileBag);
                didExchange = true;
                continue;
            }

            String[] parts = move.split(" ");

            if (parts.length != 3) {
                System.out.println("ERROR! Please enter a valid move. Format should be <LETTER> <ROW> <COLUMN>.");
                continue;
            }

            char letter = Character.toUpperCase(parts[0].charAt(0));
            int row, col;

            try {
                row = Integer.parseInt(parts[1]);
                col = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                System.out.println("ERROR! Invalid coordinates. Must be integers.");
                continue;
            }

            Tile selectedTile = null;
            for (Tile tile : player.getHand()) {
                if (tile.getLetter() == letter) {
                    selectedTile = tile;
                    break;
                }
            }

            if (selectedTile == null) {
                System.out.println("ERROR! You don't have the letter '" + letter + "' in your hand.");
                continue;
            }

            if (board.placeTile(row, col, selectedTile)) {
                selectedTile.setCoords(row, col);
                placedTiles.add(selectedTile);
                System.out.println(player.getName() + " placed " + letter + " at (" +  row + "," + col + ").");
            }
            else {
                System.out.println("ERROR! Invalid move. Position is either already occupied, or out of bounds.");
            }
        }
    }

    public boolean ValidateMove(boolean firstTurn) {
        boolean sameRow = true;
        boolean sameCol = true;
        for (int i = 1; i < placedTiles.size(); i++) {
            if (placedTiles.get(i).getX() != placedTiles.get(i-1).getX()) sameCol = false;
            if (placedTiles.get(i).getY() != placedTiles.get(i-1).getY()) sameRow = false;
        }

        if (!sameRow && !sameCol) {
            System.out.println("ERROR! All tiles must be placed on the same row or coloumn");
            return false;
        }

        int start;
        int end;
        int otherCoord;

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
        System.out.println(start + "\t" + end + "\t" + otherCoord);

        if (!board.haveEmptySpace(start, end, otherCoord, sameRow)) {
            System.out.println("Error! The placed tiles must all be used to form one word.");
            return false;
        }
        
        if (firstTurn && board.getTile(Board.CENTER, Board.CENTER) == null) {
            System.out.println("ERROR! The first word must pass through the center.");
            return false;
        }

        for (String word: board.getPlacedWords()) {
            if (!(dictionary.isValidWord(word))) return false; 
        }

        return true;
    }

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
            System.out.print("Please enter the number of players (2-4): ");
            numPlayers = Integer.parseInt(scanner.nextLine());
            if  (numPlayers < 2 || numPlayers > 4) {
                System.out.println("ERROR! Please enter a number between 2 and 4.");
                continue;
            }
            break;
        }

        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter a name for Player " + i + ": ");
            String name = scanner.nextLine();
            game.addPlayer(name);
        }

        game.startGame();

        while (playable) {
            game.displayBoard();
            while (true) {
                game.makeMove(game.getCurrentPlayer());
                if (game.didExchange){
                    game.didExchange = false;
                    game.nextTurn();
                    break;
                }
                else if (game.ValidateMove(firstTurn)) {
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
                    for (Tile tile : placedTiles) {
                        game.getCurrentPlayer().addTile(board.removeTile(tile.getX(), tile.getY()));
                    }
                    placedTiles.clear();
                }
            }

            if (tilebag.isEmpty()) {
                playable = false;
            }
        }
    }
}
