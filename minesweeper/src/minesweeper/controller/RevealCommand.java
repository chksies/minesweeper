package minesweeper.controller;

import minesweeper.model.Board;
import minesweeper.model.Cell;
import minesweeper.model.Cover;
import minesweeper.model.PlainCover;
import minesweeper.model.RevealedCover;
import minesweeper.patterns.Game;

// ─────────────────────────────────────────────────────────────────────────────
//  REVEAL COMMAND
//  Reveals a cell. Snapshots every cover before executing so undo() can
//  restore the whole board, including cells opened by a blank-cell flood-fill.
// ─────────────────────────────────────────────────────────────────────────────
public class RevealCommand implements Command {

    private final Game game;
    private final int  row;
    private final int  col;
    private Cover[][]  coversBefore;   // saved before execution; null if nothing happened

    public RevealCommand(Game game, int row, int col) {
        this.game = game;
        this.row  = row;
        this.col  = col;
    }

    @Override
    public void execute() {
        Board board = game.getBoard();
        Cell  cell  = board.getCell(row, col);

        coversBefore = null;
        if (cell.isRevealed() || cell.isFlagged()) return;  // nothing to do

        coversBefore = board.snapshotCovers();   // save every cover for undo
        game.reveal(row, col);
    }

    @Override
    public void undo() {
        if (coversBefore == null) return;
        // Restore all covers — re-hides the clicked cell and any flood-filled cells
        game.getBoard().restoreCovers(coversBefore);
    }

    @Override
    public String describe() {
        return String.format("Reveal (%d, %d)", row, col);
    }
}


// ─────────────────────────────────────────────────────────────────────────────
//  FLAG COMMAND
//  Cycles a cell's flag state (Plain → Flag → Question → Plain).
//  Stores the previous Cover so undo() can restore it exactly.
// ─────────────────────────────────────────────────────────────────────────────
class FlagCommand implements Command {

    private final Game game;
    private final int  row;
    private final int  col;
    private Cover      previousCover;

    FlagCommand(Game game, int row, int col) {
        this.game = game;
        this.row  = row;
        this.col  = col;
    }

    @Override
    public void execute() {
        previousCover = game.getBoard().getCell(row, col).getCover();
        game.flag(row, col);
    }

    @Override
    public void undo() {
        game.getBoard().setCover(row, col, previousCover);
    }

    @Override
    public String describe() {
        return String.format("Flag (%d, %d)", row, col);
    }
}
