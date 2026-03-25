package minesweeper.model;

/** Cell flagged as containing a mine. */
public class FlagCover extends Cover {
    @Override public String getSymbol()   { return "[F]"; }
    @Override public boolean isRevealed() { return false; }
}
