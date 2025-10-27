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

    public boolean hasNeighbor(int row, int col) {
        int[][] directions = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : directions) {
            int r = row + d[0], c = col + d[1];
            if (r >= 0 && r < SIZE && c >= 0 && c < SIZE && board[r][c] != null)
                return true;
        }
        return false;
    }

    public boolean haveEmptySpace(int start, int end, int otherCoord, boolean direction) {
        for (int i = start; i != end + 1; i++) {
            if (direction) {
                if (getTile(otherCoord, i) == null) return false;
            }
            else {
                if (getTile(i, otherCoord) == null) return false;
            }
        }
        return true;
    }

    public ArrayList<String> getPlacedWords() {
        ArrayList<String> words = new ArrayList<String>();
        String currentWord = "";
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (!currentWord.equals("") && board[i][j] == null) {
                    words.add(currentWord);
                    currentWord = "";
                }
                else if (board[i][j] != null) {
                    currentWord += board[i][j].getLetter();
                }
            }
        }

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (!currentWord.equals("") && board[j][i] == null) {
                    words.add(currentWord);
                    currentWord = "";
                }
                else if (board[j][i] != null) {
                    currentWord += board[j][i].getLetter();
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
