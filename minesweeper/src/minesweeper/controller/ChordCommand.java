package minesweeper.controller;

import minesweeper.model.Board;
import minesweeper.model.Cover;
import minesweeper.patterns.Game;

import java.util.List;

// ─────────────────────────────────────────────────────────────────────────────
//  CHORD COMMAND
//  On a revealed number whose adjacent flags match its value, reveals all the
//  remaining unflagged neighbours at once. Snapshots every cover first so
//  undo() restores the whole board, like RevealCommand.
// ─────────────────────────────────────────────────────────────────────────────
public class ChordCommand implements Command {

    private final Game game;
    private final int  row;
    private final int  col;
    private Cover[][]  coversBefore;   // null if the chord did nothing

    public ChordCommand(Game game, int row, int col) {
        this.game = game;
        this.row  = row;
        this.col  = col;
    }

    @Override
    public void execute() {
        Board board = game.getBoard();
        coversBefore = null;

        List<int[]> targets = board.chordTargets(row, col);
        if (targets.isEmpty()) return;

        coversBefore = board.snapshotCovers();
        for (int[] t : targets) {
            game.reveal(t[0], t[1]);   // a mine hit moves Game to LostState, which ignores the rest
        }
    }

    @Override
    public void undo() {
        if (coversBefore == null) return;
        game.getBoard().restoreCovers(coversBefore);
    }

    @Override
    public String describe() {
        return String.format("Chord (%d, %d)", row, col);
    }
}
