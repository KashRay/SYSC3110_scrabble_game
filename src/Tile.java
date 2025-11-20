public class Tile {
    private char letter;
    private final int score;
    private int x;
    private int y;

    /**
     * Constructs a new Tile with the specified letter and score.
     * The letter is automatically converted to uppercase for consistency.
     *
     * @param letter the character displayed on the tile
     * @param score  the point value of the tile
     */
    public Tile(char letter, int score) {
        this.letter = Character.toUpperCase(letter);
        this.score = score;
    }

    /**
     * Sets the (x, y) position of this tile on the board.
     *
     * @param x the x-coordinate of the tile
     * @param y the y-coordinate of the tile
     */
    public void setCoords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Sets the letter of the tile. Used for blank tiles.
     *
     * @param letter the letter to be set
     */
    public void setLetter(char letter) {
        this.letter = Character.toUpperCase(letter);
    }

    /**
     * Returns the letter displayed on this tile.
     *
     * @return the tile’s letter
     */
    public char getLetter() {
        return this.letter;
    }

    /**
     * Returns the score value of this tile.
     *
     * @return the tile’s score
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Returns the x-coordinate of this tile on the board.
     *
     * @return the tile’s x position
     */
    public int getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate of this tile on the board.
     *
     * @return the tile’s y position
     */
    public int getY() {
        return this.y;
    }

    /**
     * Returns a string representation of this tile, including its letter and score.
     * For example, a tile with the letter 'A' and a score of 1 would be represented as "A1".
     *
     * @return a string representation of the tile
     */
    @Override
    public String toString() {
        // Concatenate the letter and score for easy display
        return this.letter + "" + this.score;
    }
}
