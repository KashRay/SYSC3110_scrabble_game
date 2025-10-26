public record Tile(char letter, int score) {
    public Tile(char letter, int score) {
        this.letter = Character.toUpperCase(letter);
        this.score = score;
    }

    @Override
    public String toString() {
        return letter + "" + score;
    }
}
