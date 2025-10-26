public record Move(String word, int startRow, int startCol, Direction direction, Player player) {
    public Move(String word, int startRow, int startCol, Direction direction, Player player) {
        this.word = word.toUpperCase();
        this.startRow = startRow;
        this.startCol = startCol;
        this.direction = direction;
        this.player = player;
    }
}
