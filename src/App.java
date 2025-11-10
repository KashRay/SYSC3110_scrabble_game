import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class App extends JFrame implements ScrabbleView {
    Game game;
    ScrabbleController controller;
    JTextArea topText;
    JButton[] squares;
    JTextArea scoreField;
    ArrayList<JButton> tiles;
    JButton done;
    JButton exchange;

    public App() {
        super("Scrabble");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        
        game = new Game();
        game.addView(this);

        controller = new ScrabbleController(this, game);

        topText = new JTextArea("SCRABBLE!");
        
        JButton button;

        JPanel middle = new JPanel();
        middle.setLayout(new FlowLayout());

        JPanel board = new JPanel();
        board.setLayout(new GridLayout(Board.SIZE, Board.SIZE));
        squares = new JButton[Board.SIZE * Board.SIZE];
        for (int i = 0; i < (Board.SIZE * Board.SIZE); i++) {
            button = new JButton();
            button.setText("_");
            if (i == 112) button.setBackground(Color.RED);
            button.setActionCommand("B " + button.getText() + " " + Integer.toString(i / Board.SIZE) + " " + Integer.toString(i % Board.SIZE));
            button.addActionListener(controller);
            button.setPreferredSize(new Dimension(50, 50));
            button.setEnabled(false);
            squares[i] = button;
            board.add(button);
        }

        scoreField = new JTextArea();

        middle.add(board);
        middle.add(scoreField);

        JPanel hand = new JPanel();
        hand.setLayout(new FlowLayout());
        tiles = new ArrayList<JButton>();
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

        this.add(topText);
        this.add(middle);
        this.add(hand);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 1000);
        this.setVisible(true);

        game.startGame();
    }

    public void enableBoard() {
        for (JButton square : squares) {
            if (!square.getText().equals("")) {
                square.setEnabled(true);
            }
        }
    }

    public void disableBoard() {
        for (JButton square : squares) {
            square.setEnabled(false);
        }
    }

    public void enableHand() {
        for (JButton tile : tiles) {
            tile.setEnabled(true);
        }
    }

    public void disableHand() {
        for (JButton tile : tiles) {
            tile.setEnabled(false);
        }
    }

    public void hideTile(int i) {
        tiles.get(i).setVisible(false);
    }

    public void enableExchange() {
        exchange.setEnabled(true);
    }

    public void disableExchange() {
        exchange.setEnabled(false);
    }

    public void updateTopText(String text) {
        topText.setText(text);
    }

    public void updateBoard(ArrayList<Tile> placedTiles, boolean validated) {
        JButton currentSquare;
        for (Tile tile : placedTiles) {
            currentSquare = squares[Board.SIZE * tile.getX() + tile.getY()];
            currentSquare.setText("" + tile.getLetter());
            if (validated) {
                currentSquare.setBackground(Color.GREEN);
            }
            else {
                currentSquare.setBackground(Color.YELLOW);
            }
        }
    }

    public void updateHand(List<Tile> hand) {
        JButton currentButton;
        for (int i = 0; i < Player.HAND_SIZE; i++) {
            currentButton = tiles.get(i);
            currentButton.setText("" + hand.get(i).getLetter());
            currentButton.setActionCommand("H " + Integer.toString(i) + " " + currentButton.getText());
            if (!currentButton.isVisible()) currentButton.setVisible(true);
        }
    }

    public void disableFirstMove() {
        done.setActionCommand("D false");
    }

    public void removePlacedTiles() {
        for (JButton button : squares) {
            if (button.getBackground() == Color.YELLOW) {
                button.setText("_");
                button.setBackground(Color.WHITE);
            }
        }

        for (JButton button : tiles) {
            if (!button.isVisible()) button.setVisible(true);
        }
    }

    public void updateScore(String newScore, int numTiles) {
        scoreField.setText(newScore + "\nTiles Remaining: " + Integer.toString(numTiles));
    }

    public void exchangeToPass() {
        exchange.setText("Pass");
    }

    public void endGame() {
        this.disableBoard();
        this.disableHand();
    }

    public static void main(String[] args) {
        new App();
    }
}