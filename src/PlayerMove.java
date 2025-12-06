import java.io.Serializable;

public record PlayerMove(int totalScore, String mainWord) implements Serializable{
    @Override
    public String toString() {
        return mainWord + " (" + totalScore + " pts)";
    }
}
