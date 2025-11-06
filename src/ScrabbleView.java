import java.util.List;
import java.util.ArrayList;

public interface ScrabbleView {
    void updateTopText(String text);
    void updateBoard(ArrayList<Tile> placedTiles);
    void updateHand(List<Tile> hand);
}