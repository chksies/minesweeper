package minesweeper.patterns;

/**
 * Simple wall-clock timer.
 * Tracks elapsed seconds since start().
 */
public class Timer {
    private long startMs = 0;
    private long stopMs  = 0;
    private boolean running = false;

    public void start() {
        startMs = System.currentTimeMillis();
        stopMs  = 0;
        running = true;
    }

    public void stop() {
        if (running) {
            stopMs  = System.currentTimeMillis();
            running = false;
        }
    }

    public void reset() {
        startMs = 0;
        stopMs  = 0;
        running = false;
    }

    /** Elapsed time in whole seconds (rounds down). */
    public int getSeconds() {
        long endMs = running ? System.currentTimeMillis() : stopMs;
        return (int) ((endMs - startMs) / 1000);
    }

    public boolean isRunning() { return running; }
}
