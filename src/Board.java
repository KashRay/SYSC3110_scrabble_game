import java.io.File;
import java.io.Serializable;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

public class Board implements Serializable {
    public static final int SIZE = 15;
    public static final int CENTER = Board.SIZE / 2;
    public static final long serialVersionUID = 1L;
    
    public enum tileType {Normal, DL, TL, DW, TW}
    public static tileType[][] premiumTiles = {
        {tileType.TW, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.TW}, 
        {tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal}, 
        {tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal}, 
        {tileType.DL, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.DL}, 
        {tileType.Normal, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.Normal}, 
        {tileType.Normal, tileType.TL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TL, tileType.Normal}, 
        {tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal}, 
        {tileType.TW, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.TW}, 
        {tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal}, 
        {tileType.Normal, tileType.TL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TL, tileType.Normal}, 
        {tileType.Normal, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.Normal}, 
        {tileType.DL, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.DL}, 
        {tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal}, 
        {tileType.Normal, tileType.DW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DW, tileType.Normal}, 
        {tileType.TW, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.Normal, tileType.TW, tileType.Normal, tileType.Normal, tileType.Normal, tileType.DL, tileType.Normal, tileType.Normal, tileType.TW}};
    
    private final Tile[][] board;

    public Board() {
        board = new Tile[SIZE][SIZE];
    }

    /**
     * Checks whether the given coordinates are within the bounds of the board.
     *
     * @param row the row index
     * @param col the column index
     * @return {@code true} if the position is within bounds, otherwise {@code false}
     */
    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    /**
     * Attempts to place a tile at the given position on the board.
     * Placement is only successful if the position is within bounds and currently empty.
     *
     * @param row  the row position
     * @param col  the column position
     * @param tile the tile to place
     * @return {@code true} if the tile was placed successfully, otherwise {@code false}
     */
    public boolean placeTile(int row, int col, Tile tile) {
        if (isInBounds(row, col) && board[row][col] == null) {
            board[row][col] = tile;
            return true;
        }
        return false;
    }

    /**
     * Removes and returns a tile from a given position on the board.
     *
     * @param row the row index
     * @param col the column index
     */
    public void removeTile(int row, int col) {
        board[row][col] = null;
    }

    /**
     * Retrieves the tile at a specific position on the board.
     *
     * @param row the row coordinate
     * @param col the column coordinate
     * @return the {@link Tile} at that position, or {@code null} if the cell is empty or out of bounds
     */
    public Tile getTile(int row, int col) {
        if (!isInBounds(row, col)) return null;
        return board[row][col];
    }

    /**
     * Checks whether all cells between the given start and end coordinates
     * (either in a row or column) are filled.
     * Used to ensure that a newly placed word forms a continuous sequence.
     *
     * @param start      the starting index (row or column)
     * @param end        the ending index (row or column)
     * @param otherCoord the fixed coordinate (depending on direction)
     * @param direction  {@code true} for horizontal, {@code false} for vertical
     * @return {@code true} if there are no empty spaces between start and end, otherwise {@code false}
     */
    public boolean haveEmptySpace(int start, int end, int otherCoord, boolean direction) {
        for (int i = start; i <= end; i++) {
            if (!direction) {
                // Vertical check
                if (getTile(i, otherCoord) == null) return false;
            }
            else {
                // Horizontal check
                if (getTile(otherCoord, i) == null) return false;
            }
        }
        return true;
    }

    /**
     * Determines whether the specified board position has any neighboring tiles.
     * A neighbor is any non-null tile located directly above, below, to the left,
     * or to the right of the given (row, col) position.
     *
     * @param row the row index of the position being checked
     * @param col the column index of the position being checked
     * @return true if at least one adjacent tile exists; false otherwise
     */
    public boolean hasNeighbor(int row, int col) {
        if (this.getTile(row - 1, col) != null) return true;
        if (this.getTile(row + 1, col) != null) return true;
        if (this.getTile(row, col - 1) != null) return true;
        if (this.getTile(row, col + 1) != null) return true;
        return false;
    }

    /**
     * Checks whether a word placement on the board is valid according to Scrabble rules.
     * A valid placement must adhere to the following:
     * Fit within board boundaries
     * No conflict with existing tiles (unless matching the same letter)
     * On the first turn, pass through the center square
     * On all subsequent turns, connect to existing tiles
     *
     *
     * @param word         The word being placed.
     * @param row          Starting row position.
     * @param col          Starting column position.
     * @param isHorizontal True if the word is placed left-to-right; false if top-to-bottom.
     * @param firstTurn    True if this is the first move of the game.
     * @return True if the placement is valid; false otherwise.
     */
    public boolean isValidPlacement(String word, int row, int col, boolean isHorizontal, boolean firstTurn) {
        boolean connects = false;
        boolean crossesCenter = false;

        //Boundary check
        if (isHorizontal) {
            if (col + word.length() > Board.SIZE) return false;
        }
        else {
            if (row + word.length() > Board.SIZE) return false;
        }

        //Collision and connection check
        for (int i = 0; i < word.length(); i++) {
            Tile tileOnBoard = this.getTile(row, col);

            if (row == Board.CENTER && col == Board.CENTER) crossesCenter = true;
            if (tileOnBoard != null) {
                if (tileOnBoard.getLetter() != word.charAt(i)) return false;
                connects = true;
            }
            else {
                if (!firstTurn && hasNeighbor(row, col)) connects = true;
            }

            if (isHorizontal) col++;
            else row++;
        }

        return firstTurn ? crossesCenter : connects;
    }

    /**
     * Imports a custom Scrabble board layout from an XML file.
     * The XML file must contain a series of row elements, each containing
     * space-separated integers describing the premium tile layout.
     * Tile encoding:
     * 1 = Double Letter (DL)
     * 2 = Triple Letter (TL)
     * 3 = Double Word (DW)
     * 4 = Triple Word (TW)
     * Any other value = Normal tile
     * If the file is missing or invalid, an error message is displayed.
     */
    public void importCustomBoard() {
        try {
            File xmlFile = new File(JOptionPane.showInputDialog("Enter the name of the XML file (with the file extension)"));

            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);

            doc.getDocumentElement().normalize();

            NodeList rows = doc.getElementsByTagName("row");

            for (int i = 0; i < SIZE; i++) {
                Element rowElement = (Element) rows.item(i);

                String[] values = rowElement.getTextContent().trim().split("\\s+");

                for (int j = 0; j < SIZE; j++) {
                    int value = Integer.parseInt(values[j]);

                    switch (value) {
                        case 1: premiumTiles[i][j] = tileType.DL; break;
                        case 2: premiumTiles[i][j] = tileType.TL; break;
                        case 3: premiumTiles[i][j] = tileType.DW; break;
                        case 4: premiumTiles[i][j] = tileType.TW; break;
                        default: premiumTiles[i][j] = tileType.Normal;
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "ERROR: File not found or file not valid");
        }
    }

    /**
     * Returns a visual representation of the current board state.
     * <p>
     * Each tile is displayed using its letter, and empty cells are marked with '--'.
     *
     * @return a formatted string representing the board grid
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] != null) {
                    sb.append(board[i][j]).append(" ");
                }
                else {
                    sb.append("-- ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
