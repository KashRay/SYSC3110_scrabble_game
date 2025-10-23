public class Board {
    public static final int SIZE = 15;
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

    public Tile getTile(int row, int col) {
        if (!isInBounds(row, col)) return null;
        return board[row][col];
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
