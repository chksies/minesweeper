package minesweeper.model;

/** Cell marked with a question — player is uncertain. */
public class QuestionCover extends Cover {
    @Override public String getSymbol()   { return "[?]"; }
    @Override public boolean isRevealed() { return false; }
}
