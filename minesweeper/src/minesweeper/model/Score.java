package minesweeper.model;

/** Immutable record of a completed game result. */
public class Score implements Comparable<Score> {
    private final String playerName;
    private final int    seconds;
    private final String difficulty;  // "Beginner", "Intermediate", "Expert"

    public Score(String playerName, int seconds, String difficulty) {
        this.playerName = playerName;
        this.seconds    = seconds;
        this.difficulty = difficulty;
    }

    public String getPlayerName() { return playerName; }
    public int    getSeconds()    { return seconds; }
    public String getDifficulty() { return difficulty; }

    /** Lower time = better score. */
    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.seconds, other.seconds);
    }

    @Override
    public String toString() {
        return String.format("%-12s  %3ds  [%s]", playerName, seconds, difficulty);
    }
}
