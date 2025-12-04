import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * The App class represents the main graphical user interface (GUI)
 * for the Scrabble game. It implements the ScrabbleView interface
 * and serves as both the view and partial controller in an MVC structure.
 *
 * This class handles layout and user interactions through Swing components.
 * It displays the game board, player hand, score, and control buttons such as
 * "Done" and "Exchange". User actions are processed by a ScrabbleController
 * instance which communicates with the Game model.
 *
 *
 * @version 1.0
 */
public class App extends JFrame implements ScrabbleView {
    Game game;
    ScrabbleController controller;
    JTextArea topText;
    JButton[] squares;
    JTextArea scoreField;
    ArrayList<JButton> tiles;
    JButton done;
    JButton exchange;

    /**
     * Constructs the Scrabble application window, initializes all GUI elements,
     * connects the model and controller, and starts a new game.
     */
    public App() {
        super("Scrabble");

        // Use a vertical layout for the main window: text, board, and hand stacked top to bottom
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        
        // Initialize model and connect this view
        game = new Game();
        game.addView(this);

        // Initialize controller and link it to this view and the model
        controller = new ScrabbleController(this, game);

        topText = new JTextArea("SCRABBLE!");
        
        JButton button;

        JPanel middle = new JPanel();
        middle.setLayout(new FlowLayout());

        // Create board grid
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(Board.SIZE, Board.SIZE));
        squares = new JButton[Board.SIZE * Board.SIZE];

        // Populate each board square with a button
        for (int i = 0; i < (Board.SIZE * Board.SIZE); i++) {
            button = new JButton();
            button.setText("_");
            int x = i / Board.SIZE;
            int y = i % Board.SIZE;
            //Set background color based on the tile type
            switch (Board.premiumTiles[x][y]) {
                case DL:
                    button.setBackground(Color.CYAN);
                    break;
                case TL:
                    button.setBackground(Color.BLUE);
                    break;
                case DW:
                    button.setBackground(Color.YELLOW);
                    break;
                case TW:
                    button.setBackground(Color.RED);
                    break;
                default:
                    break;
            }
            // Assign unique action command encoding board position and content
            button.setActionCommand("B " + button.getText() + " " + x + " " + y);
            button.addActionListener(controller);
            button.setPreferredSize(new Dimension(50, 50));
            button.setEnabled(false);
            squares[i] = button;
            board.add(button);
        }

        scoreField = new JTextArea();

        middle.add(board);
        middle.add(scoreField);

        // --- Bottom section: player hand + action buttons
        JPanel hand = new JPanel();
        hand.setLayout(new FlowLayout());
        tiles = new ArrayList<JButton>();

        // Initialize player's hand (7 tiles)
        for (int i = 0; i < Player.HAND_SIZE; i++) {
            button = new JButton();
            button.addActionListener(controller);
            button.setPreferredSize(new Dimension(50, 50));
            tiles.add(button);
            hand.add(button);
        }

        JPanel endOptions = new JPanel();
        endOptions.setLayout(new BoxLayout(endOptions, BoxLayout.Y_AXIS));
        
        done = new JButton("Done");
        done.setActionCommand("D true");
        done.addActionListener(controller);
        
        exchange = new JButton("Exchange");
        exchange.setActionCommand("E");
        exchange.addActionListener(controller);

        endOptions.add(done);
        endOptions.add(exchange);

        hand.add(endOptions);

        JMenuBar menuBar = new JMenuBar();
        
        JMenu file = new JMenu("File");
        
        JMenuItem save = new JMenuItem("Save");
        save.setActionCommand("S");
        save.addActionListener(controller);
        
        JMenuItem load = new JMenuItem("Load");
        load.setActionCommand("L");
        load.addActionListener(controller);
        
        file.add(save);
        file.add(load);

        JMenu edit = new JMenu("Edit");

        JMenuItem importB = new JMenuItem("Import Custom Board");
        importB.setActionCommand("I");
        importB.addActionListener(controller);

        edit.add(importB);

        menuBar.add(file);
        menuBar.add(edit);

        this.setJMenuBar(menuBar);
        this.add(topText);
        this.add(middle);
        this.add(hand);

        // Configure the JFrame window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);
        this.setVisible(true);

        game.startGame();
    }

    /** 
    * Enables all playable board squares. 
    */
    public void enableBoard() {
        for (JButton square : squares) {
            if (!square.getText().isEmpty()) {
                square.setEnabled(true);
            }
        }
    }

    /** 
    * Disables all board squares. 
    */
    public void disableBoard() {
        for (JButton square : squares) {
            square.setEnabled(false);
        }
    }

    /** 
    * Enables all player hand tiles. 
    */
    public void enableHand() {
        for (JButton tile : tiles) {
            tile.setEnabled(true);
        }
    }

    /** 
    * Disables all player hand tiles. 
    */
    public void disableHand() {
        for (JButton tile : tiles) {
            tile.setEnabled(false);
        }
    }

    /** 
    * Hides a specific tile from the player's hand (used when placed on board). 
    */
    public void hideTile(int i) {
        tiles.get(i).setVisible(false);
    }

    /**
    * Enables the "Done" button.
    */
    public void enableDone() {
        done.setEnabled(true);
    }

    /** 
    * Disables the "Done" button. 
    */
    public void disableDone() {
        done.setEnabled(false);
    }

    /** 
    * Enables the "Exchange" button. 
    */
    public void enableExchange() {
        exchange.setEnabled(true);
    }

    /** 
    * Disables the "Exchange" button. 
    */
    public void disableExchange() {
        exchange.setEnabled(false);
    }

    /**
     * Updates the text area at the top of the window with a new message.
     *
     * @param text message or status to display
     */
    public void updateTopText(String text) {
        topText.setText(text);
    }

    /**
     * Updates the game board to show tiles that have been placed.
     *
     * @param placedTiles list of tiles to display
     * @param validated whether the move has been validated (true = permanent)
     */
    public void updateBoard(ArrayList<Tile> placedTiles, boolean validated) {
        JButton currentSquare;
        for (Tile tile : placedTiles) {
            currentSquare = squares[Board.SIZE * tile.getX() + tile.getY()];
            currentSquare.setText("" + tile.getLetter());
            // Green for confirmed moves, magenta for pending ones
            if (validated) {
                currentSquare.setBackground(Color.GREEN);
            }
            else {
                currentSquare.setBackground(Color.MAGENTA);
            }
        }
    }

    /**
     * Updates the player's hand display to show their current tiles.
     * Hidden tiles (already placed) are re-shown after the turn ends.
     *
     * @param hand list of tiles currently held by the player
     */
    public void updateHand(List<Tile> hand) {
        JButton currentButton;
        for (int i = 0; i < Player.HAND_SIZE; i++) {
            currentButton = tiles.get(i);
            if (i >= hand.size()) {
                if (currentButton.isVisible()) currentButton.setVisible(false);
            }
            else {
                currentButton.setText("" + hand.get(i).getLetter());
                currentButton.setActionCommand("H " + i + " " + currentButton.getText());
                if (!currentButton.isVisible()) currentButton.setVisible(true);
            }
        }
    }

    /** 
    * Prevents the "Done" button from marking the first move as valid. 
    */
    public void disableFirstMove() {
        done.setActionCommand("D false");
    }

    /**
     * Removes all tiles that were placed but not yet confirmed (yellow tiles).
     * Also restores hidden tiles back to the player's hand.
     */
    public void removePlacedTiles() {
        for (int i = 0; i < (Board.SIZE * Board.SIZE); i++) {
            JButton button = squares[i];
            if (button.getBackground() != Color.GREEN) {
                button.setText("_");
                int x = i / Board.SIZE;
                int y = i % Board.SIZE;
                //Set background color based on the tile type
                switch (Board.premiumTiles[x][y]) {
                    case DL:
                        button.setBackground(Color.CYAN);
                        break;
                    case TL:
                        button.setBackground(Color.BLUE);
                        break;
                    case DW:
                        button.setBackground(Color.YELLOW);
                        break;
                    case TW:
                        button.setBackground(Color.RED);
                        break;
                    default:
                        button.setBackground(null);
                        break;
                }
            }
        }

        for (JButton button : tiles) {
            if (!button.isVisible()) button.setVisible(true);
        }
    }

    /**
     * Updates the score display area with the player's score and remaining tiles.
     *
     * @param newScore the player's current score
     * @param numTiles number of tiles left in the bag
     */
    public void updateScore(String newScore, int numTiles) {
        scoreField.setText(newScore + "\nTiles Remaining: " + numTiles + "\n\nCYAN:\tDouble Letter\nBLUE:\tTriple Letter\nYELLOW:\tDouble Word\nRED:\tTriple Word\n\nLetter Score Values:\nA:\t1\nB:\t3\nC:\t3\nD:\t2\nE:\t1\nF:\t4\nG:\t2\nH:\t4\nI:\t1\nJ:\t8\nK:\t5\nL:\t1\nM:\t3\nN:\t1\nO:\t1\nP:\t3\nQ:\t10\nR:\t1\nS:\t1\nT:\t1\nU:\t1\nV:\t4\nW:\t4\nX:\t8\nY:\t4\nZ:\t10");
    }

    /** 
    * Changes the "Exchange" button label to "Pass" after an exchange action. 
    */
    public void exchangeToPass() {
        exchange.setText("Pass");
    }

    /** 
    * Ends the game by disabling all user input. 
    */
    public void endGame() {
        this.disableBoard();
        this.disableHand();
    }

    /**
     * Main entry point for launching the Scrabble application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new App();
    }
}