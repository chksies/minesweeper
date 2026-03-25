package minesweeper.model;

/**
 * Abstract base for all grid cells.
 *
 * Design Pattern: TEMPLATE METHOD
 *   reveal() defines the skeleton of the reveal algorithm:
 *     1. Check if already revealed or flagged (invariant — same for all cells)
 *     2. Place RevealedCover
 *     3. Call doReveal() — the hook each subclass overrides
 *
 * Subclasses (MineCell, SafeCell) only override doReveal(),
 * never the overall reveal() protocol.
 */
public abstract class Cell {

    protected final int row;
    protected final int col;
    protected Cover cover;

    public Cell(int row, int col) {
        this.row   = row;
        this.col   = col;
        this.cover = new PlainCover();
    }

    // ── Template Method ─────────────────────────────────────────────────────

    /**
     * Skeleton algorithm — shared by all cell types.
     * @return true if the reveal actually happened (not already open / flagged)
     */
    public final boolean reveal() {
        if (cover.isRevealed()) return false;          // step 1a: already open
        if (cover instanceof FlagCover) return false;  // step 1b: flagged — skip

        cover = new RevealedCover();                   // step 2: uncover
        doReveal();                                    // step 3: subclass hook
        return true;
    }

    /**
     * Hook — each subclass defines its unique reveal behaviour.
     * MineCell  → triggers explosion logic via Board callback.
     * SafeCell  → potentially triggers recursive blank expansion.
     */
    protected abstract void doReveal();

    // ── Cover management ────────────────────────────────────────────────────

    public void cycleFlag() {
        if (cover.isRevealed()) return;
        if (cover instanceof PlainCover)    cover = new FlagCover();
        else if (cover instanceof FlagCover)    cover = new QuestionCover();
        else if (cover instanceof QuestionCover) cover = new PlainCover();
    }

    public void resetCover()            { cover = new PlainCover(); }
    public Cover getCover()             { return cover; }
    public boolean isRevealed()         { return cover.isRevealed(); }
    public boolean isFlagged()          { return cover instanceof FlagCover; }
    public int getRow()                 { return row; }
    public int getCol()                 { return col; }

    public abstract boolean isMine();
    public abstract String getDisplaySymbol(); // symbol shown when revealed
}
