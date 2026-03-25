package minesweeper.patterns;

import minesweeper.model.Board;

/**
 * GameState interface — STATE pattern.
 *
 * Each concrete state handles user actions differently.
 * Game (the Context) delegates all input to the current state object,
 * eliminating if/else chains spread across Game methods.
 */
public interface GameState {
    void onEnter(Game ctx);
    void handleReveal(Game ctx, int row, int col);
    void handleFlag(Game ctx, int row, int col);
    String getStatusMessage();
}
