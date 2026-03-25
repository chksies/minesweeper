package minesweeper.controller;

import minesweeper.model.Board;
import minesweeper.model.Cell;
import minesweeper.model.Cover;
import minesweeper.model.PlainCover;
import minesweeper.model.RevealedCover;
import minesweeper.patterns.Game;

// ─────────────────────────────────────────────────────────────────────────────
//  REVEAL COMMAND
//  Reveals a cell. Stores the cell's previous cover so undo() can restore it.
//
//  NOTE: Recursive blank-cell expansion (flood-fill) happens inside Board/Cell,
//  so undo of a blank reveal re-covers only the top-level cell clicked.
//  Full multi-cell undo would require recording all transitively revealed cells
//  — a known trade-off documented in the design.
// ─────────────────────────────────────────────────────────────────────────────
public class RevealCommand implements Command {

    private final Game game;
    private final int  row;
    private final int  col;
    private Cover      previousCover;  // saved before execution
    private boolean    wasExecuted = false;

    public RevealCommand(Game game, int row, int col) {
        this.game = game;
        this.row  = row;
        this.col  = col;
    }

    @Override
    public void execute() {
        Board board = game.getBoard();
        Cell  cell  = board.getCell(row, col);

        if (cell.isRevealed() || cell.isFlagged()) return;  // nothing to do

        previousCover = cell.getCover();   // save current cover for undo
        wasExecuted   = true;
        game.reveal(row, col);
    }

    @Override
    public void undo() {
        if (!wasExecuted) return;
        Board board = game.getBoard();
        // Restore the cover — effectively "hides" the cell again
        board.setCover(row, col, previousCover);
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
