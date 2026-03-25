package minesweeper.model;

/**
 * A cell that does NOT contain a mine.
 * Stores the count of adjacent mines and triggers recursive reveal
 * via a Board callback when the count is zero.
 */
public class SafeCell extends Cell {

    private int adjacentMines = 0;
    private Runnable onBlankReveal; // callback: Board.revealNeighbours(row,col)

    public SafeCell(int row, int col) {
        super(row, col);
    }

    public void setAdjacentMines(int count)        { this.adjacentMines = count; }
    public int  getAdjacentMines()                 { return adjacentMines; }
    public void setOnBlankReveal(Runnable callback){ this.onBlankReveal = callback; }

    @Override
    protected void doReveal() {
        // If no adjacent mines, flood-fill neighbours automatically
        if (adjacentMines == 0 && onBlankReveal != null) {
            onBlankReveal.run();
        }
    }

    @Override public boolean isMine() { return false; }

    @Override
    public String getDisplaySymbol() {
        return adjacentMines == 0 ? "   " : " " + adjacentMines + " ";
    }
}
