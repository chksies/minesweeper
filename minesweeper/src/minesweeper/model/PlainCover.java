package minesweeper.model;

/** Unrevealed cell — no flag, no question mark. */
public class PlainCover extends Cover {
    @Override public String getSymbol()   { return "[ ]"; }
    @Override public boolean isRevealed() { return false; }
}
