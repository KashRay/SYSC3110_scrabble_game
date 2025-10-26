import java.util.*;

public class Game {
    private final Board board;
    private static TileBag tileBag;
    private final List<Player> players;
    private int currentPlayer;
    private boolean isFirstMove = true;
    private final MoveValidator moveValidator;

    public Game() {
        board = new Board();
        tileBag = new TileBag();
        Dictionary dictionary = new Dictionary();
        dictionary.loadFromFile("wordlist.txt");
        players = new ArrayList<>();
        currentPlayer = 0;
        moveValidator = new MoveValidator(board, dictionary);
    }

    public void addPlayer(String name) {
        players.add(new Player(name));
    }

    public void startGame() {
        for (Player player : players) {
            player.addTile(tileBag);
        }
        isFirstMove = true;
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

    public boolean makeMove(String word, int row, int col, Direction direction) {
        Player currentPlayer = getCurrentPlayer();
        Move move = new Move(word, row, col, direction, currentPlayer);

        if (moveValidator.isValidMove(move, isFirstMove)) {
            for (int i = 0; i < word.length(); i++) {
                int c = col + (direction == Direction.HORIZONTAL ? i : 0);
                int r = row + (direction == Direction.VERTICAL ? i : 0);

                if (board.getTile(r, c) == null) {
                    Tile tile = currentPlayer.removeTileByLetter(word.charAt(i));
                    board.placeTile(r, c, tile);
                }
            }

            isFirstMove = false;
            System.out.println(currentPlayer.getName() + " successfully placed: " + word);
            nextTurn();
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();
        boolean playable = true;

        System.out.println("Welcome to SCRABBLE!");
        System.out.print("Please enter the number of players (2-4): ");
        int numPlayers = Integer.parseInt(scanner.nextLine());

        for (int i = 1; i <= numPlayers; i++) {
            System.out.print("Enter a name for Player " + i + ": ");
            String name = scanner.nextLine();
            game.addPlayer(name);
        }

        game.startGame();

        while (playable) {
            game.displayBoard();
            Player currentPlayer = game.getCurrentPlayer();

            while (true) {
                System.out.println("It's " + currentPlayer.getName() + "'s turn!");
                System.out.print("Please enter a word: ");
                String word = scanner.nextLine().toUpperCase().trim();

                System.out.print("Please enter a starting column (0-" + (Board.SIZE - 1) + "): ");
                int col = Integer.parseInt(scanner.nextLine());
                System.out.print("Please enter a starting row (0-" + (Board.SIZE - 1) + "): ");
                int row = Integer.parseInt(scanner.nextLine());
                System.out.print("Please enter a direction (H - horizontal, V - vertical): ");
                String dir = scanner.nextLine().trim().toUpperCase();
                Direction direction = dir.equals("H") ? Direction.HORIZONTAL : Direction.VERTICAL;

                if (game.makeMove(word, row, col, direction)) {
                    currentPlayer.addTile(tileBag);
                    break;
                }
                else continue;
            }

            if (tileBag.isEmpty()) {
                playable = false;
            }
        }
    }
}
