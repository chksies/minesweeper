package minesweeper.model;

/**
 * Sentinel cover placed on a cell once it has been revealed.
 * isRevealed() returns true, which tells the Board the cell is open.
 */
public class RevealedCover extends Cover {
    @Override public String getSymbol()   { return null; } // cell renders its own content
    @Override public boolean isRevealed() { return true; }
}
