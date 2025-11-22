import java.util.HashMap;
import java.util.Map;

public enum ScrabbleLetters {
    A('A', 1, 9),
    B('B', 3, 2),
    C('C', 3, 2),
    D('D', 2, 4),
    E('E', 1, 12),
    F('F', 4, 2),
    G('G', 2, 3),
    H('H', 4, 2),
    I('I', 1, 9),
    J('J', 8, 1),
    K('K', 5, 1),
    L('L', 1, 4),
    M('M', 3, 2),
    N('N', 1, 6),
    O('O', 1, 8),
    P('P', 3, 2),
    Q('Q', 10, 1),
    R('R', 1, 6),
    S('S', 1, 4),
    T('T', 1, 6),
    U('U', 1, 4),
    V('V', 4, 2),
    W('W', 4, 2),
    X('X', 8, 1),
    Y('Y', 4, 2),
    Z('Z', 10, 1),
    BLANK(' ', 0, 2);

    private final char letter;
    private final int score;
    private final int count;

    // Reverse lookup map for getting Enum by character efficiently
    private static final Map<Character, ScrabbleLetters> lookup = new HashMap<>();

    static {
        for (ScrabbleLetters sl : ScrabbleLetters.values()) {
            lookup.put(sl.letter, sl);
        }
    }

    ScrabbleLetters(char letter, int score, int count) {
        this.letter = letter;
        this.score = score;
        this.count = count;
    }

    public char getLetter() { return letter; }
    public int getScore() { return score; }
    public int getCount() { return count; }

    public static ScrabbleLetters get(char character) {
        return lookup.get(Character.toUpperCase(character));
    }
}