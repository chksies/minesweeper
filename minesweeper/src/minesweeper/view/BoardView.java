package minesweeper.view;

import minesweeper.model.Board;
import minesweeper.model.Cell;
import minesweeper.model.HighScoreManager;
import minesweeper.model.Score;
import minesweeper.patterns.Game;

import java.util.List;

/**
 * BoardView — renders the Minesweeper board as text in the terminal.
 *
 * Design principle: knows nothing about GameState transitions.
 * It only reads Board data and Game status messages.
 * Swapping this for a GUI view requires no changes to model or controller.
 */
public class BoardView {

    private static final String RESET  = "\u001B[0m";
    private static final String RED    = "\u001B[31m";
    private static final String GREEN  = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE   = "\u001B[34m";
    private static final String CYAN   = "\u001B[36m";
    private static final String BOLD   = "\u001B[1m";
    private static final String DIM    = "\u001B[2m";

    public void render(Game game) {
        Board  board = game.getBoard();
        int    rows  = board.getRows();
        int    cols  = board.getCols();

        System.out.println();
        renderHeader(game);
        renderColumnNumbers(cols);
        renderSeparator(cols);

        for (int r = 0; r < rows; r++) {
            System.out.printf("%2d |", r);
            for (int c = 0; c < cols; c++) {
                System.out.print(cellString(board.getCell(r, c)));
            }
            System.out.println("|");
        }

        renderSeparator(cols);
        System.out.println("  " + game.getStatusMessage());
        System.out.println();
    }

    private void renderHeader(Game game) {
        Board board = game.getBoard();
        int remaining = board.getMineCount() - board.getFlagCount();
        System.out.printf(BOLD + "  Minesweeper — %s   " + RESET +
                          "Mines: %s%d%s   Time: %ds%n",
                game.getDifficulty().name(),
                remaining < 0 ? RED : YELLOW,
                remaining,
                RESET,
                game.getTimer().getSeconds());
    }

    private void renderColumnNumbers(int cols) {
        System.out.print("   ");
        for (int c = 0; c < cols; c++) System.out.printf(DIM + "%3d" + RESET, c);
        System.out.println();
    }

    private void renderSeparator(int cols) {
        System.out.print("   +");
        System.out.print("---".repeat(cols));
        System.out.println("+");
    }

    private String cellString(Cell cell) {
        if (!cell.isRevealed()) {
            // Show cover symbol
            String sym = cell.getCover().getSymbol();
            if ("[F]".equals(sym)) return RED  + "[F]" + RESET;
            if ("[?]".equals(sym)) return YELLOW + "[?]" + RESET;
            return DIM + "[ ]" + RESET;
        }
        // Cell is revealed — show its content
        if (cell.isMine()) return RED + BOLD + " * " + RESET;

        String sym = cell.getDisplaySymbol().trim();
        if (sym.isEmpty()) return "   "; // blank safe cell
        int n = Integer.parseInt(sym);
        String[] colours = {"", BLUE, GREEN, RED, CYAN, RED, CYAN, BOLD, DIM};
        String colour = (n > 0 && n < colours.length) ? colours[n] : "";
        return colour + " " + n + " " + RESET;
    }

    // ── High-scores screen ────────────────────────────────────────────────────

    public void renderHighScores(String difficulty) {
        System.out.println(BOLD + "\n── High Scores: " + difficulty + " ──" + RESET);
        List<Score> scores = HighScoreManager.getInstance()
                                             .getTopScores(difficulty, 5);
        if (scores.isEmpty()) {
            System.out.println("  No scores yet.");
        } else {
            int rank = 1;
            for (Score s : scores) {
                System.out.printf("  %d. %s%n", rank++, s);
            }
        }
        System.out.println();
    }
}
