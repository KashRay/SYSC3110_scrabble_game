import java.util.ArrayList;
import java.util.List;

public class Player {
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

    public boolean removeTile(Tile tile) {
        return hand.remove(tile);
    }

    public String handToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("'s hand: ");
        for (Tile t : hand) sb.append(t).append(" ");
        return sb.toString();
    }

    @Override
    public String toString() {
        return name + " (" + score + " pts)";
    }
}
