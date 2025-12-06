import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

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
    private Game game;

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
     * mainWord validation, or tile exchange) and then updates both the game
     * and GUI accordingly.
     *
     *
     * @param event the ActionEvent triggered by a button press
     */
    public void actionPerformed(ActionEvent event) {
        // Split the action command string into its parts
        String[] command = event.getActionCommand().split(" ");

        //Store game state in undo stack if not undoing or redoing
        if (!(command[0].equals("U") || command[0].equals("R"))) {
            try {
                if (game.getUndoStack().isEmpty()) app.toggleUndo(true);
                game.storeState(game.getUndoStack());
            }
            catch (Exception e) {
                e.printStackTrace();
                System.out.println("Failed to store in undo stack.");
            }
        }
        
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
                game.clearRedoStack();
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
                game.clearRedoStack();
                break;

            /*
             * Case "D": The player clicked the "Done" button.
             * - Validate the current mainWord.
             * - If valid, proceed to the next turn.
             * - If invalid, reset the unvalidated tiles on the board.
             */
            case "D":
                if (game.validateMove(Boolean.parseBoolean(command[1]))) {
                    game.nextTurn(false);
                }
                else {
                    game.removeViewsPlacedTiles();
                }
                app.enableExchange();
                game.clearRedoStack();
                break; 
                
            /*
             * Case "E": The player chose to exchange tiles or pass.
             * - Immediately proceeds to the next turn with an exchange/pass action.
             */
            case "E":
                game.nextTurn(true);
                game.clearRedoStack();
                break;
            /*
             * Case "S": The player chooses to save the game.
             *
             */
            case "S":
                javax.swing.JFileChooser saveChooser = new javax.swing.JFileChooser();
                if (saveChooser.showSaveDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                    java.io.File saveFile = saveChooser.getSelectedFile();
                    try {
                        game.saveGame(saveFile);
                        app.updateTopText("Game Saved!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        app.updateTopText("Error saving game!");
                    }
                }
                game.clearRedoStack();
                break;

            /*
             * Case "L": The player chooses to load a game from a save file.
             *
             */
            case "L":
                javax.swing.JFileChooser loadChooser = new javax.swing.JFileChooser();
                if (loadChooser.showOpenDialog(null) == javax.swing.JFileChooser.APPROVE_OPTION) {
                    java.io.File loadFile = loadChooser.getSelectedFile();
                    try {
                        Game loadedGame = Game.loadGame(loadFile);
                        this.game = loadedGame;
                        loadedGame.addView(app);
                        loadedGame.clearUndoStack();
                        loadedGame.clearRedoStack();
                        app.refreshBoard(loadedGame);
                        app.toggleUndo(false);
                        app.toggleRedo(false);
                        app.updateTopText("Game Loaded!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        app.updateTopText("Error loading game!");
                    }
                }
                break;

            /*
             * Case "I": The player chooses to import a custom game board.
             *
             */
            case "I":
                game.importCustomBoard();
                game.clearRedoStack();
                break;

            /*
             * Case "U": The player chooses to undo to a previous game state.
             *
             */
            case "U":
                try {
                    Game loadedGame = game.undo();
                    this.game = loadedGame;
                    loadedGame.addView(app);
                    app.refreshBoard(loadedGame);
                    app.updateTopText("Undo'd Game");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to store in undo stack.");
                }
                break;
            
            /*
             * Case "R": The player chooses to redo back to the former game state.
             *
             */
            case "R":
                try {
                    Game loadedGame = game.redo();
                    this.game = loadedGame;
                    loadedGame.addView(app);
                    app.refreshBoard(loadedGame);
                    app.updateTopText("Redo'd Game");
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Failed to store in undo stack.");
                }
                break;

            default:
                break;
        }
    }
}
