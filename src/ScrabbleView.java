import java.util.List;
import java.util.ArrayList;

public interface ScrabbleView {
    void updateTopText(String text);
    void updateBoard(ArrayList<Tile> placedTiles, boolean validated);
    void updateHand(List<Tile> hand);
    void disableFirstMove();
    void removePlacedTiles();
    void updateScore(String newScore);
}