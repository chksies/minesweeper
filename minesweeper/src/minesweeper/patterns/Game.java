package minesweeper.patterns;

import minesweeper.model.Board;
import minesweeper.model.HighScoreManager;
import minesweeper.model.Score;

/**
 * Game — Context for the STATE pattern.
 *
 * Game holds a reference to the current GameState and delegates every
 * user action to it. Game itself never checks "are we playing? won? lost?"
 * — that logic lives entirely inside the state objects.
 *
 * Supported difficulties:
 *   Beginner     :  9×9,  10 mines
 *   Intermediate : 16×16, 40 mines
 *   Expert       : 16×30, 99 mines
 */
public class Game {

    // ── Difficulty presets ────────────────────────────────────────────────────
    public enum Difficulty {
        BEGINNER    ( 9,  9, 10),
        INTERMEDIATE(16, 16, 40),
        EXPERT      (16, 30, 99);

        public final int rows, cols, mines;
        Difficulty(int r, int c, int m) { rows = r; cols = c; mines = m; }
    }

    // ── State ─────────────────────────────────────────────────────────────────
    private GameState currentState;
    private Board     board;
    private Timer     timer;
    private final Difficulty difficulty;
    private String    playerName;
    private boolean   firstMove;

    public Game(Difficulty difficulty, String playerName) {
        this.difficulty = difficulty;
        this.playerName = playerName;
        this.timer      = new Timer();
        initBoard();
        setState(new PlayingState());
    }

    private void initBoard() {
        board     = new Board(difficulty.rows, difficulty.cols, difficulty.mines);
        firstMove = true;
        // Board will call back into Game when a mine is detonated
        board.setOnMineHit(() -> setState(new LostState()));
    }

    // ── State machine ─────────────────────────────────────────────────────────

    public void setState(GameState newState) {
        currentState = newState;
        currentState.onEnter(this);
    }

    // ── User actions — delegated to current state ─────────────────────────────

    public void reveal(int row, int col) {
        if (firstMove) {
            board.initialize(row, col);   // safe-first: mines placed around first click
            timer.start();
            firstMove = false;
        }
        currentState.handleReveal(this, row, col);
    }

    public void flag(int row, int col) {
        currentState.handleFlag(this, row, col);
    }

    public void reset() {
        timer.stop();
        initBoard();
        setState(new PlayingState());
    }

    // ── Win/loss helpers called by states ────────────────────────────────────

    /** Called by PlayingState after a reveal to check win condition. */
    public void checkWin() {
        if (board.isWon()) {
            setState(new WonState());
        }
    }

    /** Called by WonState.onEnter() to save the score. */
    public void recordScore() {
        timer.stop();
        int elapsed = timer.getSeconds();
        Score score = new Score(playerName, elapsed, difficulty.name());
        HighScoreManager.getInstance().addScore(score);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public Board       getBoard()         { return board; }
    public Timer       getTimer()         { return timer; }
    public GameState   getCurrentState()  { return currentState; }
    public String      getStatusMessage() { return currentState.getStatusMessage(); }
    public Difficulty  getDifficulty()    { return difficulty; }
    public String      getPlayerName()    { return playerName; }
    public boolean     isFirstMove()      { return firstMove; }
}
