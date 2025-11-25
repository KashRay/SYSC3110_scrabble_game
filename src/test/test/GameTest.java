import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.*;

public class GameTest {

    private Game game;

    @Before
    public void setUp() {
        game = new Game();
        game.addPlayer("Alice");
        game.addPlayer("Bob");
    }

    @After
    public void tearDown() {
        game = null;
    }

    @Test
    public void addPlayer() {
        Game testGame = new Game();
        testGame.addPlayer("Alice");
        testGame.addPlayer("Bob");
        assertNotNull(testGame.getCurrentPlayer());
    }

    @Test
    public void nextTurn() {
        game.nextTurn(false);
        Player first = game.getCurrentPlayer();
        game.nextTurn(false);
        Player second = game.getCurrentPlayer();
        assertNotEquals(first, second);
    }

    @Test
    public void selectAndPlaceTile() {
        Player player = game.getCurrentPlayer();
        player.addTile(new Tile('A', 1));
        player.addTile(new Tile('B', 3));
        int before = player.getHand().size();
        game.selectTile('A');
        int after = player.getHand().size();
        assertTrue(after < before);
        boolean placed = game.placeTile(7, 7);
        assertTrue(placed);
    }

    @Test
    public void validateMoveFailsWithoutCenter() {
        Player player = game.getCurrentPlayer();
        player.addTile(new Tile('A', 1));
        game.selectTile('A');
        game.placeTile(0, 0);
        boolean valid = game.validateMove(true);
        assertFalse(valid);
    }

    @Test
    public void validateMoveSuccessAtCenter() {
        Player player = game.getCurrentPlayer();
        player.addTile(new Tile('A', 1));
        game.selectTile('A');
        game.placeTile(Board.CENTER, Board.CENTER);
        boolean valid = game.validateMove(true);
        assertTrue(valid);
    }

    @Test
    public void endGameRunsWithoutError() {
        Player player1 = game.getCurrentPlayer();
        player1.addScore(10);
        game.nextTurn(false);
        Player player2 = game.getCurrentPlayer();
        player2.addScore(20);
        game.endGame();
        assertTrue(true);
    }

    @Test
    public void aiFindsValidMove() {
        Dictionary dict = new Dictionary();
        dict.getWords().add("CAT");

        Board board = new Board();  // Empty board

        AIPlayer ai = new AIPlayer("Bot");
        ai.addTile(new Tile('C', 3));
        ai.addTile(new Tile('A', 1));
        ai.addTile(new Tile('T', 1));

        Move move = ai.getBestMove(dict, board, true);

        assertNotNull("AI should find a move using CAT", move);
        assertEquals("CAT", move.word());
    }

    @Test
    public void aiReturnsNullWhenNoPlayableMove() {
        Dictionary dict = new Dictionary();
        dict.getWords().add("DOG");

        Board board = new Board();

        AIPlayer ai = new AIPlayer("Bot");
        ai.addTile(new Tile('X', 8));
        ai.addTile(new Tile('Q', 10));
        ai.addTile(new Tile('Z', 10));

        Move move = ai.getBestMove(dict, board, true);

        assertNull("AI should not find a move with unplayable letters", move);
    }

    @Test
    public void aiChoosesHighestScoringMove() {
        Dictionary dict = new Dictionary();
        dict.getWords().add("CAT");
        dict.getWords().add("AXE");

        Board board = new Board();

        AIPlayer ai = new AIPlayer("Bot");
        ai.addTile(new Tile('C', 3));
        ai.addTile(new Tile('A', 1));
        ai.addTile(new Tile('T', 1));
        ai.addTile(new Tile('A', 1));
        ai.addTile(new Tile('X', 8));
        ai.addTile(new Tile('E', 1));

        Move move = ai.getBestMove(dict, board, true);

        assertNotNull(move);
        assertEquals("AXE", move.word());  // AXE scores more than CAT
    }

    @Test
    public void aiUsesBlankTileToFormWord() {
        Dictionary dict = new Dictionary();
        dict.getWords().add("DOG");

        Board board = new Board();

        AIPlayer ai = new AIPlayer("Bot");
        ai.addTile(new Tile('D', 2));
        ai.addTile(new Tile('O', 1));
        ai.addTile(new Tile(' ', 0)); // blank tile

        Move move = ai.getBestMove(dict, board, true);

        assertNotNull("AI should use blank tile as G", move);
        assertEquals("DOG", move.word());
    }

    @Test
    public void invalidPlacementCausesFailure() {
        Dictionary dict = new Dictionary();
        dict.getWords().add("CAT");

        Board board = new Board();
        board.placeTile(Board.CENTER, Board.CENTER, new Tile('X', 8));

        AIPlayer ai = new AIPlayer("Bot");
        ai.addTile(new Tile('C', 3));
        ai.addTile(new Tile('A', 1));
        ai.addTile(new Tile('T', 1));

        Move move = ai.getBestMove(dict, board, true);

        assertNull("AI should not place tiles on occupied center", move);
    }

}
