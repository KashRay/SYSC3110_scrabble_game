import java.util.*;

public class Game {
    Scanner scanner = new Scanner(System.in);
    private final Board board;
    private final TileBag tileBag;
    private final Dictionary dictionary;
    private final List<Player> players;
    private int currentPlayer;

    public Game() {
        this.board = new Board();
        this.tileBag = new TileBag();
        this.dictionary = new Dictionary();
        this.players = new ArrayList<>();
        this.currentPlayer = 0;
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
        String move = "";

        System.out.println("Player " + player.getName() + " is making a move! To make a move, enter the command in the format <LETTER> <ROW> <COLUMN> (e.g.: 'A 7 8'). When you are done making a move, type 'done'.");
        while (!move.equals("done")) {
            move = scanner.nextLine().trim();

            if (move.equals("done")) continue;
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
                System.out.println(player.getName() + " placed " + letter + " at (" +  row + "," + col + ").");
            }
            else {
                System.out.println("ERROR! Invalid move. Position is either already occupied, or out of bounds.");
            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.addPlayer("Abdullah");
        game.addPlayer("Adrian");
        game.addPlayer("Ismael");
        game.addPlayer("Rayane");

        game.startGame();
        int turns = 4;
        while (turns > 0) {
            game.displayBoard();
            game.makeMove(game.getCurrentPlayer());
            game.nextTurn();
            turns -= 1;
        }
    }
}
