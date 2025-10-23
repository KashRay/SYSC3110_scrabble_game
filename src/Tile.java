public class Tile {
    private final char letter;
    private final  int score;

    public Tile(char letter, int score) {
        this.letter = Character.toUpperCase(letter);
        this.score = score;
    }

    public char getLetter() {
        return letter;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return letter + "" + score;
    }
}
