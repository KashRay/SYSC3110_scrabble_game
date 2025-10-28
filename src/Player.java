import java.util.ArrayList;
import java.util.List;

public class Player {
    public static final int HAND_SIZE = 7;
    private final String name;
    private final List<Tile> hand;
    private int score;

    /**
     * Constructs a new {@code Player} with the given name.
     * The player starts with an empty hand and a score of zero.
     *
     * @param name the player's name
     */
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.score = 0;
    }

    /**
     * Returns the player's name.
     *
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the player's current hand of tiles.
     * Modifications to the returned list will affect the player's hand directly.
     *
     * @return the list of tiles in the player's hand
     */
    public List<Tile> getHand() {
        return hand;
    }

    /**
     * Returns the player's current score.
     *
     * @return the player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Adds a given number of points to the player's total score.
     *
     * @param score the number of points to add
     */
    public void addScore(int score) {
        this.score += score;
    }

    /**
     * Draws tiles from the provided TileBag until the player's hand is full
     * or the bag becomes empty.
     * This ensures the player never exceeds HAND_SIZE tiles.
     *
     * @param bag the ileBag to draw tiles from
     */
    public void addTile(TileBag bag) {
        while (hand.size() <  HAND_SIZE && !bag.isEmpty()) {
            hand.add(bag.drawTile());
        }
    }

    /**
     * Adds a single Tile to the player's hand.
     * This does not enforce HAND_SIZE, it assumes validation is handled elsewhere.
     *
     * @param tile the tile to add
     */
    public void addTile(Tile tile) {
        hand.add(tile);
    }

    /**
     * Removes and returns the first Tile in the player's hand.
     * This method assumes the hand is not empty when called.
     *
     * @return the first tile in the hand
     */
    public Tile removeTile() {
        return hand.remove(0);
    }

    /**
     * Removes and returns a Tile from the player's hand that matches the given letter.
     * If multiple tiles have the same letter, only the first match is removed.
     * If no matching tile is found, the method returns null.
     *
     * @param letter the letter of the tile to remove
     * @return the removed tile, or null if no tile with the given letter was found
     */
    public Tile removeTileByLetter(char letter) {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getLetter() == letter) {
                return hand.remove(i);
            }
        }
        return null;
    }

    /**
     * Returns a string representation of the player, showing their score, name, and hand contents.
     *
     * @return a string describing the player's current state
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(score).append(" pts) ").append(name).append("'s hand: ");
        for (Tile t : hand) sb.append(t).append(" ");
        return sb.toString();
    }
}
