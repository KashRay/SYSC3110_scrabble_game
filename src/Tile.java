public class Tile {
    private char letter;
    private int score;
    private int x;
    private int y;
    
    public Tile(char letter, int score) {
        this.letter = Character.toUpperCase(letter);
        this.score = score;
    }

    public void setCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public char getLetter() {
        return this.letter;
    }

    public int getScore() {
        return this.score;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    @Override
    public String toString() {
        return this.letter + "" + this.score;
    }
}
