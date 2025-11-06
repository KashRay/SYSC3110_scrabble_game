import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class App extends JFrame {
    Game game;
    JTextArea topText;
    ArrayList<JButton> squares;
    ArrayList<JButton> tiles;

    public App() {
        super("Scrabble");
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        
        game = new Game();
        topText = new JTextArea("SCRABBLE!");
        
        JButton button;

        JPanel board = new JPanel();
        board.setLayout(new GridLayout(Board.SIZE, Board.SIZE));
        squares = new ArrayList<JButton>();
        for (int i = 0; i < (Board.SIZE * Board.SIZE); i++) {
            button = new JButton();
            button.setPreferredSize(new Dimension(50, 50));
            button.setEnabled(false);
            squares.add(button);
            board.add(button);
        }

        JPanel hand = new JPanel();
        hand.setLayout(new FlowLayout());
        tiles = new ArrayList<JButton>();
        for (int i = 0; i < 7; i++) {
            button = new JButton();
            button.setPreferredSize(new Dimension(50, 50));
            tiles.add(button);
            hand.add(button);
        }

        this.add(topText);
        this.add(board);
        this.add(hand);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000, 500);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new App();
    }
}