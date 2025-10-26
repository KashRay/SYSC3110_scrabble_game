import java.util.ArrayList;
import java.util.List;

public class MoveValidator {
    private final Board board;
    private final Dictionary dictionary;

    public MoveValidator(Board board, Dictionary dictionary) {
        this.board = board;
        this.dictionary = dictionary;
    }

    private boolean fitsOnBoard(Move move) {
        if (move.direction() == Direction.HORIZONTAL) return move.startCol() + move.word().length() <= Board.SIZE;
        else return move.startRow() + move.word().length() <= Board.SIZE;
    }

    private boolean passesThroughCenter(Move move) {
        Direction direction = move.direction();
        int length = move.word().length();
        int col = move.startCol();
        int row = move.startRow();

        int end = direction == Direction.HORIZONTAL ? col + length - 1 : row + length - 1;
        if (direction == Direction.HORIZONTAL) return Board.CENTER >= col && Board.CENTER <= end && row == Board.CENTER;
        else return  Board.CENTER >= row && Board.CENTER <= end && col == Board.CENTER;
    }

    private boolean connectsToExisting(Move move) {
        Direction direction = move.direction();
        String word = move.word();
        int col = move.startCol();
        int row = move.startRow();

        for (int i = 0; i < word.length(); i++) {
            int c = col + (direction == Direction.HORIZONTAL ? i : 0);
            int r = row + (direction == Direction.VERTICAL ? i : 0);

            if (board.getTile(r, c) != null) return true;
            if (board.hasNeighbor(r, c)) return true;
        }
        return false;
    }

    private boolean playerHasTiles(Move move) {
        Player player = move.player();
        List<Tile> tempHand = new ArrayList<>(player.getHand());

        int row = move.startRow();
        int col = move.startCol();
        String word = move.word();
        Direction direction = move.direction();

        for (int i = 0; i < word.length(); i++) {
            int c = col + (direction == Direction.HORIZONTAL ? i : 0);
            int r = row + (direction == Direction.VERTICAL ? i : 0);

            Tile tempTile = board.getTile(r, c);
            char letter = word.charAt(i);

            if (tempTile == null) {
                boolean found = false;
                for (int j = 0; j < tempHand.size(); j++) {
                    if (tempHand.get(j).letter() == letter) {
                        tempHand.remove(j);
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
            else if (tempTile.letter() != letter) return false;
        }
        return true;
    }

    public boolean isValidMove(Move move, boolean isFirstMove) {
        String word = move.word();
        int row = move.startRow();
        int col = move.startCol();

        if (!fitsOnBoard(move)) {
            System.out.println("ERROR! Word does not fit on the board.");
            return false;
        }

        if (!dictionary.isValidWord(word)) {
            System.out.println("ERROR! Word is not valid.");
            return false;
        }

        if (isFirstMove) {
            if (!passesThroughCenter(move)) {
                System.out.println("ERROR! The first word must pass through the center.");
                return false;
            }
        }
        else if (!connectsToExisting(move)) {
            System.out.println("ERROR! Word must connect to an already existing one.");
            return false;
        }

        if (!playerHasTiles(move)) {
            System.out.println("ERROR! The player is missing a required tile.");
            return false;
        }

        return true;
    }
}
