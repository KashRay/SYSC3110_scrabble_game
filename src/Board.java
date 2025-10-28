import java.util.ArrayList;

public class Board {
    public static final int SIZE = 15;
    public static final int CENTER = Board.SIZE / 2;
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
     * @return the removed {@link Tile}, or {@code null} if the position was empty
     */
    public Tile removeTile(int row, int col) {
        Tile removedTile = board[row][col];
        board[row][col] = null;
        return removedTile;
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
        for (int i = start; i != end + 1; i++) {
            if (!direction) {
                // Vertical check
                if (getTile(otherCoord, i) == null) return false;
            }
            else {
                // Horizontal check
                if (getTile(i, otherCoord) == null) return false;
            }
        }
        return true;
    }


    /**
     * Retrieves all words currently placed on the board.
     * <p>
     * Words are detected by scanning each row and column and concatenating
     * sequences of adjacent tiles.
     *
     * @return an {@link ArrayList} containing all placed words
     */
    public ArrayList<String> getPlacedWords() {
        ArrayList<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();

        // Horizontal word scan
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if ((!currentWord.isEmpty()) && board[i][j] == null) {
                    words.add(currentWord.toString());
                    currentWord = new StringBuilder();
                }
                else if (board[i][j] != null) {
                    currentWord.append(board[i][j].getLetter());
                }
            }
        }

        // Vertical word scan
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (!currentWord.toString().isEmpty() && board[j][i] == null) {
                    words.add(currentWord.toString());
                    currentWord = new StringBuilder();
                }
                else if (board[j][i] != null) {
                    currentWord.append(board[j][i].getLetter());
                }
            }
        }

        return words;
    }


    /**
     * Returns a visual representation of the current board state.
     * <p>
     * Each tile is displayed using its letter, and empty cells are marked with "--".
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
