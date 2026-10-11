package minesweeper;

import minesweeper.controller.CommandManager;
import minesweeper.model.HighScoreManager;
import minesweeper.patterns.Game;
import minesweeper.patterns.Game.Difficulty;
import minesweeper.view.BoardView;

import java.util.Scanner;

/**
 * Main — entry point and game loop.
 *
 * Commands:
 *   r <row> <col>   — reveal a cell
 *   f <row> <col>   — cycle flag on a cell
 *   c <row> <col>   — chord: reveal neighbours of a satisfied number
 *   u               — undo last action
 *   redo            — redo last undone action
 *   reset           — start a new game (same difficulty)
 *   scores          — show high scores
 *   quit            — exit
 */
public class Main {

    public static void main(String[] args) {
        Scanner  scanner = new Scanner(System.in);
        BoardView view   = new BoardView();

        System.out.println("=== MINESWEEPER ===");
        String name = prompt(scanner, "Enter your name: ");
        Difficulty diff = chooseDifficulty(scanner);

        Game           game    = new Game(diff, name);
        CommandManager manager = new CommandManager(game);

        view.render(game);

        while (true) {
            String line = prompt(scanner, "> ").trim().toLowerCase();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String   cmd   = parts[0];

            try {
                switch (cmd) {
                    case "r": case "reveal": {
                        int[] cell = parseCell(parts, game);
                        if (cell == null) continue;
                        manager.reveal(cell[0], cell[1]);
                        break;
                    }
                    case "f": case "flag": {
                        int[] cell = parseCell(parts, game);
                        if (cell == null) continue;
                        manager.flag(cell[0], cell[1]);
                        break;
                    }
                    case "c": case "chord": {
                        int[] cell = parseCell(parts, game);
                        if (cell == null) continue;
                        if (game.isOver()) {
                            System.out.println("Game is over. Type 'reset' for a new game.");
                            continue;
                        }
                        if (game.getBoard().chordTargets(cell[0], cell[1]).isEmpty()) {
                            System.out.println("Nothing to chord: pick a revealed number whose flag count matches it,"
                                    + " with unrevealed neighbours left.");
                            continue;
                        }
                        manager.chord(cell[0], cell[1]);
                        break;
                    }
                    case "u": case "undo": {
                        if (manager.canUndo()) manager.undo();
                        else if (game.isOver()) System.out.println("Game is over. Type 'reset' for a new game.");
                        else System.out.println("Nothing to undo.");
                        break;
                    }
                    case "redo": {
                        if (manager.canRedo()) manager.redo();
                        else System.out.println("Nothing to redo.");
                        break;
                    }
                    case "reset": {
                        game    = new Game(diff, name);
                        manager = new CommandManager(game);
                        System.out.println("New game started!");
                        break;
                    }
                    case "scores": {
                        view.renderHighScores(diff.name());
                        continue;
                    }
                    case "history": {
                        manager.printHistory();
                        continue;
                    }
                    case "quit": case "exit": {
                        System.out.println("Thanks for playing!");
                        return;
                    }
                    default:
                        printHelp();
                        continue;
                }
            } catch (RuntimeException e) {
                System.out.println("Something went wrong: " + e.getMessage());
                continue;
            }

            view.render(game);

            // After each move check if game ended and prompt accordingly
            String status = game.getStatusMessage();
            if (status.startsWith("YOU WIN") || status.startsWith("BOOM")) {
                System.out.println("Type 'reset' for a new game or 'quit' to exit.");
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────

    private static Difficulty chooseDifficulty(Scanner scanner) {
        System.out.println("Choose difficulty:");
        System.out.println("  1. Beginner     ( 9× 9, 10 mines)");
        System.out.println("  2. Intermediate (16×16, 40 mines)");
        System.out.println("  3. Expert       (16×30, 99 mines)");
        while (true) {
            String choice = prompt(scanner, "Enter 1/2/3: ").trim();
            switch (choice) {
                case "1": return Difficulty.BEGINNER;
                case "2": return Difficulty.INTERMEDIATE;
                case "3": return Difficulty.EXPERT;
                default: System.out.println("Please enter 1, 2, or 3.");
            }
        }
    }

    /**
     * Parse "<cmd> <row> <col>" and validate it against the board size.
     * Prints a specific message and returns null if the input is unusable.
     */
    private static int[] parseCell(String[] parts, Game game) {
        if (parts.length != 3) {
            System.out.println("Usage: " + parts[0] + " <row> <col>   (example: " + parts[0] + " 3 4)");
            return null;
        }
        int r, c;
        try {
            r = Integer.parseInt(parts[1]);
            c = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            System.out.println("Row and column must be whole numbers.");
            return null;
        }
        int rows = game.getBoard().getRows();
        int cols = game.getBoard().getCols();
        if (r < 0 || r >= rows || c < 0 || c >= cols) {
            System.out.printf("Out of range. Rows are 0-%d and columns are 0-%d.%n", rows - 1, cols - 1);
            return null;
        }
        return new int[]{r, c};
    }

    private static String prompt(Scanner scanner, String msg) {
        System.out.print(msg);
        return scanner.hasNextLine() ? scanner.nextLine() : "quit";
    }

    private static void printHelp() {
        System.out.println("Commands:");
        System.out.println("  r <row> <col>  — reveal cell");
        System.out.println("  f <row> <col>  — cycle flag (plain/flag/question)");
        System.out.println("  c <row> <col>  — chord: reveal neighbours of a number with enough flags");
        System.out.println("  u              — undo last action");
        System.out.println("  redo           — redo last undone action");
        System.out.println("  reset          — start new game");
        System.out.println("  scores         — show high scores");
        System.out.println("  quit           — exit");
    }
}
