import java.util.List;
import java.util.ArrayList;

/**
 * The ScrabbleView interface defines the methods that any view class
 * (such as a GUI or console-based UI) must implement to display and update
 * the state of a Scrabble game.
 * 
 * This interface follows the Model–View–Controller (MVC) architecture,
 * where the Game model notifies the view (through this interface)
 * whenever the state changes, so the view can update the user interface accordingly.
 * 
 */
public interface ScrabbleView {
    /**
     * Updates the message displayed at the top of the view.
     * Typically used to show turn information, prompts, or status messages.
     *
     * @param text the message to display at the top of the UI
     */
    void updateTopText(String text);

    /**
     * Updates the board display to reflect placed tiles.
     * 
     * Called after tiles have been placed on the board. If the move has been validated,
     * the affected squares may be shown with a permanent color (e.g., green),
     * otherwise temporarily (e.g., yellow).
     * 
     *
     * @param placedTiles a list of Tile objects that were placed on the board
     * @param validated   true if the move was validated as a legal word;
     *                    false if still pending validation
     */
    void updateBoard(ArrayList<Tile> placedTiles, boolean validated);

    /**
     * Updates the player’s hand display to reflect the current set of tiles.
     *
     * @param hand a list of Tile objects currently in the player's hand
     */
    void updateHand(List<Tile> hand);

    /**
     * Disables the ability to make the special first move.
     * 
     * Typically used to prevent the player from placing the initial move rules
     * after it has already been completed.
     * 
     */
    void disableFirstMove();

    /**
     * Removes tiles that were placed on the board but not validated.
     *
     * Used to reset the board view if an invalid move is detected or
     * if the player cancels their turn.
     * 
     */
    void removePlacedTiles();

    /**
     * Updates the score display and shows how many tiles remain.
     *
     * @param newScore  the current player’s score as a string
     * @param numTiles  the number of tiles remaining in the game
     */
    void updateScore(String newScore, int numTiles);

    /**
     * Changes the exchange button to a "Pass" button.
     * 
     * This reflects the transition from tile exchange mode to pass mode,
     * typically after the player has already exchanged tiles once.
     * 
     */
    void exchangeToPass();

    /**
     * Disables further interaction with the game board and hand.
     *
     * Called when the game ends, ensuring that no more moves or actions
     * can be performed by the player.
     *
     */
    void endGame();
}