import java.util.ArrayList;

public class Board {
    public static final int SIZE = 15;
    public static final int CENTER = Board.SIZE / 2;
    private final Tile[][] board;

    public Board() {
        board = new Tile[SIZE][SIZE];
    }

    public boolean isInBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    public boolean placeTile(int row, int col, Tile tile) {
        if (isInBounds(row, col) && board[row][col] == null) {
            board[row][col] = tile;
            return true;
        }
        return false;
    }

    public Tile removeTile(int row, int col) {
        Tile removedTile = board[row][col];
        board[row][col] = null;
        return removedTile;
    }

    public Tile getTile(int row, int col) {
        if (!isInBounds(row, col)) return null;
        return board[row][col];
    }

    public boolean haveEmptySpace(int start, int end, int otherCoord, boolean direction) {
        for (int i = start; i != end + 1; i++) {
            if (!direction) {
                if (getTile(otherCoord, i) == null) return false;
            }
            else {
                if (getTile(i, otherCoord) == null) return false;
            }
        }
        return true;
    }

    public ArrayList<String> getPlacedWords() {
        ArrayList<String> words = new ArrayList<>();
        StringBuilder currentWord = new StringBuilder();
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
