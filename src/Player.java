import java.util.ArrayList;
import java.util.List;

public class Player {
    public static final int HAND_SIZE = 7;
    private final String name;
    private final List<Tile> hand;
    private int score;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.score = 0;
    }

    public String getName() {
        return name;
    }

    public List<Tile> getHand() {
        return hand;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void addTile(TileBag bag) {
        while (hand.size() <  HAND_SIZE && !bag.isEmpty()) {
            hand.add(bag.drawTile());
        }
    }

    public Tile removeTileByLetter(char letter) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).letter() == letter) {
                return hand.remove(i);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(score).append(" pts) ").append(name).append("'s hand: ");
        for (Tile t : hand) sb.append(t).append(" ");
        return sb.toString();
    }
}
