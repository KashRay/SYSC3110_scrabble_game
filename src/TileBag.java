import java.util.*;

public class TileBag {
    private final List<Tile> tiles;
    private final Random rand;

    public TileBag() {
        tiles = new ArrayList<>();
        rand = new Random();
        initializeTiles();
        shuffle();
    }

    private void addTile(char letter, int value, int count) {
        for (int i = 0; i < count; i++) {
            tiles.add(new Tile(letter, value));
        }
    }

    private void initializeTiles() {
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

    public void shuffle() {
        Collections.shuffle(tiles, rand);
    }

    public Tile drawTile() {
        if (tiles.isEmpty()) return null;
        return tiles.removeFirst();
    }

    public boolean isEmpty() {
        return tiles.isEmpty();
    }

    public int size() {
        return tiles.size();
    }

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
