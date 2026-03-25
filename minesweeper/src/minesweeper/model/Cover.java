package minesweeper.model;

/**
 * Abstract Cover — represents what the player sees on top of a Cell.
 * Subclasses: PlainCover, FlagCover, QuestionCover.
 *
 * Design pattern: used by the State pattern indirectly (each Cover type
 * determines how clicks are handled), and supports open/closed principle —
 * new cover types can be added without modifying Cell.
 */
public abstract class Cover {
    public abstract String getSymbol();   // text-UI symbol
    public abstract boolean isRevealed(); // true only when no cover remains
}
