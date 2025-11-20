import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The ScrabbleController class handles user interactions
 * between the Scrabble GUI (the App) and the game logic (the Game).
 *
 * It interprets button clicks (tiles, board squares, and action buttons)
 * and updates the model and view accordingly using the MVC pattern.
 * 
 */
public class ScrabbleController implements ActionListener{
    private final App app;
    private final Game game;

    /**
     * Constructs a new ScrabbleController that manages communication
     * between the given App and Game.
     *
     * @param app  the graphical user interface component (view)
     * @param game the game logic object (model)
     */
    public ScrabbleController(App app, Game game) {
        super();
        this.app = app;
        this.game = game;
    }

    /*
     * Handles all button actions triggered in the GUI.
     * 
     * This method parses the ActionCommand of the pressed button
     * to determine what type of action occurred (hand tile selection, board placement,
     * move validation, or tile exchange) and then updates both the game
     * and GUI accordingly.
     *
     *
     * @param event the ActionEvent triggered by a button press
     */
    public void actionPerformed(ActionEvent event) {
        // Split the action command string into its parts
        String[] command = event.getActionCommand().split(" ");
        switch (command[0]) {

            /*
             * Case "H": A tile from the player's hand was selected.
             * - Disable the rest of the hand (only one tile can be active)
             * - Enable the board so the player can place it
             * - Hide the selected tile from the hand
             * - Notify the Game of the selected tile
             */

            case "H":
                int selectedTile = Integer.parseInt(command[1]);
                app.disableHand();
                app.enableBoard();
                app.hideTile(selectedTile);
                app.disableDone();
                app.disableExchange();

                char letter = ' ';
                if (command.length > 2) {
                    letter = command[2].charAt(0);
                }
                game.selectTile(letter);
                break;

            /*
             * Case "B": A board square was clicked.
             * - Attempt to place the selected tile on the board.
             * - If successful, re-enable the player's hand for further actions.
             */
            case "B":
                if (game.placeTile(Integer.parseInt(command[2]), Integer.parseInt(command[3]))) {
                    app.enableHand();
                    app.disableBoard();
                    app.enableDone();
                }
                break;

            /*
             * Case "D": The player clicked the "Done" button.
             * - Validate the current move.
             * - If valid, proceed to the next turn.
             * - If invalid, reset the unvalidated tiles on the board.
             */
            case "D":
                if (game.ValidateMove(Boolean.parseBoolean(command[1]))) {
                    game.nextTurn(false);
                }
                else {
                    game.removeViewsPlacedTiles();
                }
                app.enableExchange();
                break; 
                
            /*
             * Case "E": The player chose to exchange tiles or pass.
             * - Immediately proceeds to the next turn with an exchange/pass action.
             */
            case "E":
                game.nextTurn(true);
                break;
            default:
                break;
        }
    }
}
