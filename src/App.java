import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The App class represents the main GUI for a simplified Scrabble game.
 * It creates the main window, initializes the board, the player's hand,
 * and communicates with the Game model through the ScrabbleView interface.
 * 
 * 
 * @version 1.0
 */

public class App extends JFrame implements ScrabbleView{
    Game game;
    JTextArea topText;
    JButton[] squares;
    ArrayList<JButton> tiles;

    /**
    * Constructs the Scrabble GUI, initializes all components,
    * links the view to the {@link Game} instance, and starts gameplay.
    */
    public App() {
        super("Scrabble");

        // Use a vertical layout for the main window: text, board, and hand stacked top to bottom
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        
        // Create the game model and register this GUI as one of its views
        game = new Game();
        game.addView(this);

        // Initialize the top text area with the title
        topText = new JTextArea("SCRABBLE!");
        
        JButton button;

        // Create the Scrabble board
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(Board.SIZE, Board.SIZE));
        squares = new JButton[Board.SIZE * Board.SIZE];

        // Initialize each square on the board
        for (int i = 0; i < (Board.SIZE * Board.SIZE); i++) {
            button = new JButton();
            button.setPreferredSize(new Dimension(50, 50));
            button.setEnabled(false);
            squares[i] = button;
            board.add(button);
        }

        // --- Create the player's hand
        JPanel hand = new JPanel();
        hand.setLayout(new FlowLayout());
        tiles = new ArrayList<JButton>();

        // Initialize 7 buttons representing the player's tiles
        for (int i = 0; i < 7; i++) {
            button = new JButton();
            button.setPreferredSize(new Dimension(50, 50));
            tiles.add(button);
            hand.add(button);
        }

        // Add all main components to the JFrame
        this.add(topText);
        this.add(board);
        this.add(hand);

        // Configure the JFrame window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 500);
        this.setVisible(true);

        game.play();
    }

    /**
     * Updates the text displayed at the top of the window.
     * 
     * @param text the new message or status to display
     */
    public void updateTopText(String text) {
        topText.setText(text);
    }

    /**
     * Updates the visual state of the Scrabble board
     * based on tiles that have been placed by players.
     * 
     * @param placedTiles a list of tiles with their positions and letters
     */
    public void updateBoard(ArrayList<Tile> placedTiles) {
        for (Tile tile : placedTiles) {
            squares[Board.SIZE * tile.getX() + tile.getY()].setText("" + tile.getLetter());
        }
    }

    /**
     * Updates the player's hand display.
     * Currently a placeholder for future implementation.
     * 
     * @param hand a list of {@link Tile} objects representing the player's current tiles
     */
    public void updateHand(List<Tile> hand) {

    }

    /**
     * The main entry point for the application.
     * Initializes and displays the Scrabble GUI.
     * 
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new App();
    }
}