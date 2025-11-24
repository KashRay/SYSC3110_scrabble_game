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
}
