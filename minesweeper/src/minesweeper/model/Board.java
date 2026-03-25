package minesweeper.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The game board — a 2-D grid of Cell objects.
 *
 * Responsibilities:
 *   - Mine placement (randomised, safe-first)
 *   - Adjacent-mine counting
 *   - reveal(r,c) and flag(r,c) operations (called by Commands)
 *   - Recursive blank-cell flood-fill
 *   - Win detection
 *
 * Board knows nothing about GameState, UI, or undo/redo.
 * It communicates mine-hit events through a callback injected by Game.
 */
public class Board {

    private final int rows;
    private final int cols;
    private final int mineCount;
    private final Cell[][] cells;

    private Runnable onMineHit;         // injected by Game → triggers state transition
    private boolean mineHit = false;

    public Board(int rows, int cols, int mineCount) {
        this.rows      = rows;
        this.cols      = cols;
        this.mineCount = mineCount;
        this.cells     = new Cell[rows][cols];
        // Pre-fill with blank SafeCells so the board can render before the first click
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                cells[r][c] = new SafeCell(r, c);
    }

    public void setOnMineHit(Runnable callback) { this.onMineHit = callback; }

    // ── Initialisation ───────────────────────────────────────────────────────

    /**
     * Place mines avoiding the cell clicked first, then compute neighbour counts.
     * Safe-first guarantee: firstRow/firstCol is never a mine.
     */
    public void initialize(int firstRow, int firstCol) {
        mineHit = false;

        // Build list of all positions except the first click
        List<int[]> positions = new ArrayList<>();
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (!(r == firstRow && c == firstCol))
                    positions.add(new int[]{r, c});

        Collections.shuffle(positions);

        // Mark mine positions
        boolean[][] isMine = new boolean[rows][cols];
        for (int i = 0; i < mineCount; i++) {
            int[] pos = positions.get(i);
            isMine[pos[0]][pos[1]] = true;
        }

        // Create cells
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                final int fr = r, fc = c;
                if (isMine[r][c]) {
                    MineCell mine = new MineCell(r, c);
                    mine.setOnDetonation(() -> handleMineHit());
                    cells[r][c] = mine;
                } else {
                    SafeCell safe = new SafeCell(r, c);
                    safe.setOnBlankReveal(() -> revealNeighbours(fr, fc));
                    cells[r][c] = safe;
                }
            }
        }

        // Compute adjacent mine counts
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!cells[r][c].isMine()) {
                    ((SafeCell) cells[r][c]).setAdjacentMines(countAdjacentMines(r, c));
                }
            }
        }
    }

    private int countAdjacentMines(int r, int c) {
        int count = 0;
        for (int[] n : neighbours(r, c))
            if (cells[n[0]][n[1]].isMine()) count++;
        return count;
    }

    private List<int[]> neighbours(int r, int c) {
        List<int[]> result = new ArrayList<>();
        for (int dr = -1; dr <= 1; dr++)
            for (int dc = -1; dc <= 1; dc++) {
                if (dr == 0 && dc == 0) continue;
                int nr = r + dr, nc = c + dc;
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols)
                    result.add(new int[]{nr, nc});
            }
        return result;
    }

    // ── Operations (called by Commands) ──────────────────────────────────────

    /** Reveal a cell. Returns false if cell was already revealed or flagged. */
    public boolean reveal(int r, int c) {
        return cells[r][c].reveal();
    }

    /** Cycle a cell's flag state (Plain → Flag → Question → Plain). */
    public void cycleFlag(int r, int c) {
        cells[r][c].cycleFlag();
    }

    /** Set a cell's cover directly (used by Command.undo()). */
    public void setCover(int r, int c, Cover cover) {
        cells[r][c].cover = cover;
    }

    /** Reveal all mines — called by LostState.onEnter(). */
    public void revealAllMines() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (cells[r][c].isMine() && !cells[r][c].isRevealed())
                    cells[r][c].cover = new RevealedCover();
    }

    // ── Recursive flood-fill ─────────────────────────────────────────────────

    private void revealNeighbours(int r, int c) {
        for (int[] n : neighbours(r, c))
            cells[n[0]][n[1]].reveal(); // each SafeCell(0) will recurse further
    }

    // ── Callbacks ────────────────────────────────────────────────────────────

    private void handleMineHit() {
        if (!mineHit) {
            mineHit = true;
            if (onMineHit != null) onMineHit.run();
        }
    }

    // ── Queries ──────────────────────────────────────────────────────────────

    public boolean isWon() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (!cells[r][c].isMine() && !cells[r][c].isRevealed())
                    return false;
        return true;
    }

    public int getFlagCount() {
        int count = 0;
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < cols; c++)
                if (cells[r][c].isFlagged()) count++;
        return count;
    }

    public Cell getCell(int r, int c) { return cells[r][c]; }
    public int  getRows()             { return rows; }
    public int  getCols()             { return cols; }
    public int  getMineCount()        { return mineCount; }
    public boolean wasMineHit()       { return mineHit; }
}
