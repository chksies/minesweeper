package minesweeper.controller;

/**
 * Command interface — COMMAND pattern.
 *
 * Every player action (reveal, flag) is wrapped in a Command object.
 * This enables:
 *   • Undo/Redo via CommandManager's history stack
 *   • Decoupling of Game (sender) from Board (receiver)
 *   • Easy addition of new actions (e.g. ChordReveal) without touching Board
 */
public interface Command {
    /** Execute the action and apply it to the Board. */
    void execute();

    /** Reverse the action, restoring prior state. */
    void undo();

    /** Human-readable description (useful for debug/logging). */
    String describe();
}
