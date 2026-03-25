package minesweeper.patterns;

import minesweeper.model.Board;

// ─────────────────────────────────────────────────────────────────────────────
//  PLAYING STATE
//  Normal gameplay: reveals and flags are processed.
//  After each reveal, win condition is checked.
// ─────────────────────────────────────────────────────────────────────────────
class PlayingState implements GameState {

    @Override
    public void onEnter(Game ctx) {
        // Nothing special on entry — board is already initialised by Game.reset()
    }

    @Override
    public void handleReveal(Game ctx, int row, int col) {
        boolean revealed = ctx.getBoard().reveal(row, col);
        if (revealed && !ctx.getBoard().wasMineHit()) {
            ctx.checkWin();   // mine hit transitions via Board callback, not here
        }
    }

    @Override
    public void handleFlag(Game ctx, int row, int col) {
        ctx.getBoard().cycleFlag(row, col);
    }

    @Override
    public String getStatusMessage() {
        return "Good luck — reveal all safe cells!";
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  WON STATE
//  All safe cells are revealed. Reveals and flags are silently ignored.
//  Score is recorded on entry.
// ─────────────────────────────────────────────────────────────────────────────
class WonState implements GameState {

    @Override
    public void onEnter(Game ctx) {
        ctx.getTimer().stop();
        ctx.recordScore();
    }

    @Override
    public void handleReveal(Game ctx, int row, int col) {
        // Game is over — ignore input
    }

    @Override
    public void handleFlag(Game ctx, int row, int col) {
        // Game is over — ignore input
    }

    @Override
    public String getStatusMessage() {
        return "YOU WIN!  Congratulations!";
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  LOST STATE
//  A mine was detonated. All mines are revealed. Input is ignored.
// ─────────────────────────────────────────────────────────────────────────────
class LostState implements GameState {

    @Override
    public void onEnter(Game ctx) {
        ctx.getTimer().stop();
        ctx.getBoard().revealAllMines();   // show where all mines were
    }

    @Override
    public void handleReveal(Game ctx, int row, int col) {
        // Game is over — ignore input
    }

    @Override
    public void handleFlag(Game ctx, int row, int col) {
        // Game is over — ignore input
    }

    @Override
    public String getStatusMessage() {
        return "BOOM! You hit a mine. Game over.";
    }
}
