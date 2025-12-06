import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.io.File;

import static org.junit.Assert.*;

public class GameTest {

    private Game game;

    @Before
    public void setUp() {
        game = new Game();
        game.addPlayer("Alice");
        game.addPlayer("Bob");
        board = new Board();
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
        assertEquals("AXE", move.word());
    }

    @Test
    public void aiUsesBlankTileToFormWord() {
        Dictionary dict = new Dictionary();
        dict.getWords().add("DOG");

        Board board = new Board();

        AIPlayer ai = new AIPlayer("Bot");
        ai.addTile(new Tile('D', 2));
        ai.addTile(new Tile('O', 1));
        ai.addTile(new Tile(' ', 0));

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
    private Board board;

    @Test
    public void testValidPlacement_FirstTurn_CrossesCenter() {
        String word = "HELLO";
        int row = Board.CENTER;
        int col = Board.CENTER - 2;

        boolean result = board.isValidPlacement(word, row, col, true, true);

        assertTrue("Word crossing center on first turn should be valid.", result);
    }

    @Test
    public void testInvalidPlacement_FirstTurn_DoesNotCrossCenter() {
        String word = "HELLO";
        int row = 5;
        int col = 5;

        boolean result = board.isValidPlacement(word, row, col, true, true);

        assertFalse("First turn placement must cross center.", result);
    }

    @Test
    public void testInvalidPlacement_WordRunsOffBoard() {
        String word = "HELLO";
        int row = 0;
        int col = Board.SIZE - 3;

        boolean result = board.isValidPlacement(word, row, col, true, false);

        assertFalse("Word that exceeds board boundaries should be invalid.", result);
    }

    @Test
    public void testInvalidPlacement_ConflictingLetters() {
        board.placeTile(7, 7, new Tile('A', 1));

        String word = "HELLO";

        boolean result = board.isValidPlacement(word, 7, 7, true, false);

        assertFalse("Conflict: existing 'A' does not match 'H'.", result);
    }

    @Test
    public void testValidPlacement_MatchingExistingLetters() {
        board.placeTile(7, 7, new Tile('H', 4));

        String word = "HELLO";

        boolean result = board.isValidPlacement(word, 7, 7, true, false);

        assertTrue("Matching tile at starting location should allow placement.", result);
    }

    @Test
    public void testValidPlacement_ConnectsToNeighbor() {
        board.placeTile(8, 7, new Tile('A', 1));

        String word = "HELLO";

        boolean result = board.isValidPlacement(word, 7, 7, true, false);

        assertTrue("Placement touching neighbor should be valid.", result);
    }

    @Test
    public void testUndoRestoresPreviousState() throws Exception {
        Game game = new Game();
        game.addPlayer("P1");
        game.addPlayer("P2");

        game.storeState(game.getUndoStack());

        Tile t = new Tile('A', 1);
        game.getCurrentPlayer().addTile(t);
        game.placeTile(Board.CENTER, Board.CENTER);

        game.storeState(game.getUndoStack());

        Game previous = game.undo();

        assertNull(previous.getBoard().getTile(Board.CENTER, Board.CENTER));
    }

    @Test
    public void testRedoRestoresExactState() throws Exception {
        Game game = new Game();
        game.addPlayer("P1");
        game.addPlayer("P2");

        for (Player p : game.getPlayers()) {
            while (p.getHand().size() < Player.HAND_SIZE) {
                p.addTile(new Tile('A', 1));
            }
        }

        game.selectTile('Z');

        game.storeState(game.getUndoStack());

        Tile t = new Tile('A', 1);
        game.getCurrentPlayer().addTile(t);
        game.placeTile(Board.CENTER, Board.CENTER);

        game.storeState(game.getUndoStack());

        Game expected = game;

        Game undone = game.undo();
        Game redone = undone.redo();

        assertTrue(expected.equals(redone));
    }

    @Test
    public void testEqualsSameState() throws Exception {
        Game g1 = new Game();
        Game g2 = new Game();

        assertTrue(g1.equals(g2));
    }

    @Test
    public void testEqualsDifferentState() throws Exception {
        Game g1 = new Game();
        Game g2 = new Game();

        g1.addPlayer("P1");
        g2.addPlayer("P1");

        g1.addPlayer("P2");
        g2.addPlayer("P2");

        g1.getCurrentPlayer().addTile(new Tile('A', 1));

        assertFalse(g1.equals(g2));
    }


    @Test
    public void testEqualsWithNonGameObject() {
        Game g = new Game();
        assertFalse(g.equals("not a game"));
    }

    @Test
    public void testSaveAndLoadGame() throws Exception {
        Game original = new Game();

        original.addPlayer("P1");
        original.addPlayer("P2");

        for (Player p : original.getPlayers()) {
            while (p.getHand().size() < Player.HAND_SIZE) {
                p.addTile(new Tile('A', 1));
            }
        }

        original.selectTile('Z');

        Tile placed = new Tile('C', 3);
        original.getCurrentPlayer().addTile(placed);
        original.placeTile(Board.CENTER, Board.CENTER);

        File tempFile = File.createTempFile("scrabble_test_save", ".dat");
        tempFile.deleteOnExit();
        original.saveGame(tempFile);

        Game loaded = Game.loadGame(tempFile);

        for (Player p : loaded.getPlayers()) {
            while (p.getHand().size() < Player.HAND_SIZE) {
                p.addTile(new Tile('A', 1));
            }
        }

        loaded.selectTile('Z');

        assertTrue(original.equals(loaded));
    }



}
