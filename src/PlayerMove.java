public record PlayerMove(int totalScore, String mainWord) {
    @Override
    public String toString() {
        return mainWord + " (" + totalScore + " pts)";
    }
}
