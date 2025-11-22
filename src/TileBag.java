import java.util.*;

/**
 * The TileBag class represents the bag of tiles used in a Scrabble game.
 *
 * It stores all available tiles, handles random shuffling, and allows tiles
 * to be drawn one at a time. The class also supports re-adding tiles, which
 * can be used for returning exchanged tiles to the bag.
 *
 *
 * This class encapsulates tile management logic so that the Game
 * class can easily access and manipulate tiles without manually tracking their distribution.
 */
public class TileBag {
    private final List<Tile> tiles;
    private final Random rand;

    /**
     * Constructs a new TileBag and initializes it with
     * the standard Scrabble tile distribution.
     *
     * Once initialized, the bag is automatically shuffled to randomize tile order.
     *
     */
    public TileBag() {
        tiles = new ArrayList<>();
        rand = new Random();
        initializeTiles();
        shuffle();
    }


    /**
     * Adds a specific number of tiles with the given letter and value to the bag.
     *
     * This method is mainly used internally during initialization to populate
     * the bag with the correct distribution of Scrabble tiles.
     *
     *
     * @param letter the letter assigned to the tile (A–Z)
     * @param value  the point value of the letter
     * @param count  how many of this letter should be added
     */
    private void addTile(char letter, int value, int count) {
        for (int i = 0; i < count; i++) {
            tiles.add(new Tile(letter, value));
        }
    }

    /**
     * Adds an individual tile back into the bag.
     *
     * This can be used to return exchanged tiles or unplayed tiles
     * to the bag during gameplay.
     *
     *
     * @param tile the Tile object to be added
     */
    public void addTile(Tile tile) {
        tiles.add(tile);
    }

    /**
     * Initializes the tile bag with the standard Scrabble letter distribution.
     *
     * Each tile is created with its respective point value and quantity.
     * Currently, blank tiles are not implemented.
     *
     */
    private void initializeTiles() {
        tiles.clear();
        for (ScrabbleLetters letter : ScrabbleLetters.values()) {
            addTile(letter.getLetter(), letter.getScore(), letter.getCount());
        }
    }

    /**
     * Randomly shuffles all tiles in the bag.
     * 
     * This ensures that the order of drawn tiles is unpredictable, simulating
     * the randomness of drawing tiles in a real Scrabble game.
     * 
     */
    public void shuffle() {
        Collections.shuffle(tiles, rand);
    }

    /**
     * Draws a single tile from the bag.
     * 
     * If the bag is empty, null is returned.
     * 
     *
     * @return the drawn Tile, or null if the bag is empty
     */
    public Tile drawTile() {
        if (tiles.isEmpty()) return null;
        return tiles.removeFirst();
    }

    /**
     * Checks whether the bag is empty.
     *
     * @return true if no tiles remain, false otherwise
     */
    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    /**
     * Returns the number of tiles remaining in the bag.
     *
     * @return the current size of the tile bag
     */
    public int size() {
        return tiles.size();
    }

    /**
     * Returns a string representation of the tile bag,
     * listing all tiles currently contained.
     *
     * @return a formatted string of the bag’s contents
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TileBag [");
        for (Tile tile : tiles) {
            sb.append(tile).append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
