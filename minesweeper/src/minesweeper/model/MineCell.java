package minesweeper.model;

/**
 * A cell that contains a mine.
 *
 * doReveal() notifies the Board via a callback so the game can
 * transition to the LOST state without MineCell knowing about Game.
 */
public class MineCell extends Cell {

    private Runnable onDetonation; // callback injected by Board

    public MineCell(int row, int col) {
        super(row, col);
    }

    public void setOnDetonation(Runnable callback) {
        this.onDetonation = callback;
    }

    @Override
    protected void doReveal() {
        if (onDetonation != null) {
            onDetonation.run();
        }
    }

    @Override public boolean isMine()              { return true; }
    @Override public String  getDisplaySymbol()    { return " * "; }
}
