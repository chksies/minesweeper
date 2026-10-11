package minesweeper.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * HighScoreManager — SINGLETON pattern.
 *
 * Why Singleton?
 *   Both Game (writes a score on win) and HighScoreScreen (reads scores to
 *   display them) must share the exact same list. A Singleton guarantees one
 *   shared instance regardless of where getInstance() is called.
 *
 * Implementation:
 *   Private constructor + static getInstance() with lazy initialisation.
 *   Thread-safe via synchronised block (double-checked locking).
 */
public class HighScoreManager {

    // ── Singleton infrastructure ──────────────────────────────────────────────
    private static volatile HighScoreManager instance;

    private HighScoreManager() {   // private — no external instantiation
        load();
    }

    public static HighScoreManager getInstance() {
        if (instance == null) {                        // first check (no lock)
            synchronized (HighScoreManager.class) {
                if (instance == null) {                // second check (with lock)
                    instance = new HighScoreManager();
                }
            }
        }
        return instance;
    }

    // ── Data ─────────────────────────────────────────────────────────────────
    private static final int MAX_SCORES_PER_DIFFICULTY = 5;
    private final List<Score> scores = new ArrayList<>();

    // ── Operations ───────────────────────────────────────────────────────────

    public void addScore(Score score) {
        scores.add(score);
        Collections.sort(scores);
        save();
    }

    // ── Persistence ──────────────────────────────────────────────────────────
    // One score per line: difficulty <TAB> seconds <TAB> player name

    private static final Path SCORE_FILE = Paths.get("highscores.txt");

    private void load() {
        if (!Files.exists(SCORE_FILE)) return;
        try {
            for (String line : Files.readAllLines(SCORE_FILE, StandardCharsets.UTF_8)) {
                String[] parts = line.split("\t", 3);
                if (parts.length != 3) continue;           // skip malformed lines
                try {
                    scores.add(new Score(parts[2], Integer.parseInt(parts[1]), parts[0]));
                } catch (NumberFormatException ignored) { }
            }
            Collections.sort(scores);
        } catch (IOException e) {
            System.err.println("Could not read high scores: " + e.getMessage());
        }
    }

    private void save() {
        List<String> lines = new ArrayList<>();
        for (Score s : scores) {
            String name = s.getPlayerName().replaceAll("[\\t\\r\\n]", " ");
            lines.add(s.getDifficulty() + "\t" + s.getSeconds() + "\t" + name);
        }
        try {
            Files.write(SCORE_FILE, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Could not save high scores: " + e.getMessage());
        }
    }

    /**
     * Returns the top N scores for a given difficulty level, sorted by time.
     */
    public List<Score> getTopScores(String difficulty, int n) {
        List<Score> filtered = new ArrayList<>();
        for (Score s : scores)
            if (s.getDifficulty().equalsIgnoreCase(difficulty))
                filtered.add(s);
        Collections.sort(filtered);
        return filtered.subList(0, Math.min(n, filtered.size()));
    }

    public List<Score> getAllScores() {
        List<Score> copy = new ArrayList<>(scores);
        Collections.sort(copy);
        return copy;
    }

    public void clearScores() {
        scores.clear();
        save();
    }

    /** Used only for testing — resets the singleton (not for production use). */
    static void resetInstance() {
        instance = null;
    }
}
