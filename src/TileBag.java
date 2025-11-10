import java.util.*;


/**
 * The {@code TileBag} class represents the bag of letter tiles used in a Scrabble game.
 * It maintains a list of all tiles, supports random drawing, and can be shuffled.
 * 
 * This class handles tile initialization based on standard Scrabble letter
 * frequencies and point values. Each tile is represented by a Tile object.
 * 
 * Example usage:
 * TileBag bag = new TileBag();
 * Tile drawn = bag.drawTile();
 * 
 * @version 1.0
 */
public class TileBag {
    private final List<Tile> tiles;
    private final Random rand;

    /**
     * Constructs a new TileBag, initializes all Scrabble tiles,
     * and shuffles them for random drawing.
     */
    public TileBag() {
        tiles = new ArrayList<>();
        rand = new Random();
        initializeTiles();
        shuffle();
    }

    /**
     * Adds multiple copies of a given letter tile to the bag.
     *
     * @param letter the character representing the tile (e.g. 'A')
     * @param value  the point value of the tile
     * @param count  how many copies of this tile to add
     */
    private void addTile(char letter, int value, int count) {
        for (int i = 0; i < count; i++) {
            tiles.add(new Tile(letter, value));
        }
    }

    /**
     * Adds a single Tile instance to the bag.
     * 
     * @param tile the tile to add to the bag
     */
    public void addTile(Tile tile) {
        tiles.add(tile);
    }

    /**
     * Initializes the tile bag with standard Scrabble letter distributions
     * and point values (based on English-language Scrabble rules).
     */
    private void initializeTiles() {
        tiles.clear();
        addTile('A', 1, 9);
        addTile('B', 3, 2);
        addTile('C', 3, 2);
        addTile('D', 2, 4);
        addTile('E', 1, 12);
        addTile('F', 4, 2);
        addTile('G', 2, 3);
        addTile('H', 4, 2);
        addTile('I', 1, 9);
        addTile('J', 8, 1);
        addTile('K', 5, 1);
        addTile('L', 1, 4);
        addTile('M', 3, 2);
        addTile('N', 1, 6);
        addTile('O', 1, 8);
        addTile('P', 3, 2);
        addTile('Q', 10, 1);
        addTile('R', 1, 6);
        addTile('S', 1, 4);
        addTile('T', 1, 6);
        addTile('U', 1, 4);
        addTile('V', 4, 2);
        addTile('W', 4, 2);
        addTile('X', 8, 1);
        addTile('Y', 4, 2);
        addTile('Z', 10, 1);
        //Blank tiles currently not implemented
    }

    /**
     * Randomly shuffles the tiles in the bag to ensure fair drawing.
     */
    public void shuffle() {
        Collections.shuffle(tiles, rand);
    }

    /**
     * Draws a single tile from the bag. The tile is removed from the bag.
     *
     * @return the drawn Tile, or {@code null} if the bag is empty
     */
    public Tile drawTile() {
        if (tiles.isEmpty()) return null;
        return tiles.removeFirst();
    }

    /**
     * Checks whether the tile bag is empty.
     *
     * @return true if no tiles remain, otherwise {@code false}
     */
    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    /**
     * Returns the current number of tiles remaining in the bag.
     *
     * @return the number of tiles left in the bag
     */
    public int size() {
        return tiles.size();
    }

    /**
     * Returns a string representation of the bag for debugging or logging purposes.
     *
     * @return a formatted string listing all remaining tiles
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
