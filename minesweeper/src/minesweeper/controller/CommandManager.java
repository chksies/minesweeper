package minesweeper.controller;

import minesweeper.patterns.Game;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * CommandManager — Invoker in the COMMAND pattern.
 *
 * Maintains two stacks:
 *   history  — commands that have been executed (undo pops from here)
 *   redoStack — commands that have been undone  (redo pops from here)
 *
 * Any new execute() clears the redo stack (standard undo/redo behaviour).
 */
public class CommandManager {

    private static final int MAX_HISTORY = 200;  // prevent unbounded growth

    private final Deque<Command> history   = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();
    private final Game game;

    public CommandManager(Game game) {
        this.game = game;
    }

    // ── Factory helpers — create and immediately execute ──────────────────────

    public void reveal(int row, int col) {
        execute(new RevealCommand(game, row, col));
    }

    public void flag(int row, int col) {
        execute(new FlagCommand(game, row, col));
    }

    // ── Core command execution ────────────────────────────────────────────────

    public void execute(Command cmd) {
        cmd.execute();
        history.push(cmd);
        redoStack.clear();                        // new action invalidates redo

        if (history.size() > MAX_HISTORY) {
            // Drop oldest entry (bottom of deque)
            Command[] arr = history.toArray(new Command[0]);
            history.clear();
            for (int i = arr.length - 2; i >= 0; i--) history.push(arr[i]);
        }
    }

    public void undo() {
        if (history.isEmpty()) return;
        Command cmd = history.pop();
        cmd.undo();
        redoStack.push(cmd);
    }

    public void redo() {
        if (redoStack.isEmpty()) return;
        Command cmd = redoStack.pop();
        cmd.execute();
        history.push(cmd);
    }

    public boolean canUndo() { return !history.isEmpty(); }
    public boolean canRedo() { return !redoStack.isEmpty(); }

    public void clearHistory() {
        history.clear();
        redoStack.clear();
    }

    /** Print history to stdout — useful for debugging. */
    public void printHistory() {
        System.out.println("── Command History (most recent first) ──");
        for (Command c : history) System.out.println("  " + c.describe());
    }
}
