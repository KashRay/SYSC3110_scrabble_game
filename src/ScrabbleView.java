import java.util.List;
import java.util.ArrayList;


/**
 * The ScrabbleView interface defines the methods that any Scrabble
 * user interface (text-based or graphical) must implement to display
 * updates from the Game model.
 * 
 * This interface is part of the Model-View-Controller (MVC) pattern,
 * where the Game serves as the model and calls these methods
 * to update the view when the game state changes.
 * 
 * @version 1.0
 */

public interface ScrabbleView {

    /**
     * Updates the top text area (such as status messages or player prompts)
     * in the view.
     *
     * @param text the text to display at the top of the screen
     */
    void updateTopText(String text);

    /**
     * Updates the visual representation of the Scrabble board
     * after tiles have been placed.
     *
     * @param placedTiles a list of tiles with their letters and board coordinates
     */
    void updateBoard(ArrayList<Tile> placedTiles);


    /**
     * Updates the player's hand with the current set of tiles.
     *
     * @param hand a list of Tile objects representing the player's current tiles
     */
    void updateHand(List<Tile> hand);
}