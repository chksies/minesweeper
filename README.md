

## How to Build & Run

```bash
chmod +x build.sh
./build.sh
```

Or manually:
```bash
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt
java -cp out minesweeper.Main
```

## Commands (in-game)

| Command          | Action                                   |
|------------------|------------------------------------------|
| `r <row> <col>`  | Reveal a cell                            |
| `f <row> <col>`  | Cycle flag (plain → flag → question)     |
| `u`              | Undo last action                         |
| `redo`           | Redo last undone action                  |
| `reset`          | Start a new game (same difficulty)       |
| `scores`         | Show high scores for current difficulty  |
| `quit`           | Exit                                     |

## Design Patterns Used

### 1. State Pattern
**Where:** `minesweeper.patterns`
- `GameState` (interface), `PlayingState`, `WonState`, `LostState`
- `Game` is the Context. It holds the current state and delegates
  `reveal()` and `flag()` to the current state object.
- **Why:** Eliminates if/else chains checking game phase. Adding a new
  state (e.g. Paused) only requires a new class.

### 2. Command Pattern
**Where:** `minesweeper.controller`
- `Command` (interface), `RevealCommand`, `FlagCommand`, `CommandManager`
- `CommandManager` is the Invoker. It holds a history stack and redo stack.
- **Why:** Enables clean undo/redo. Each command stores the pre-action
  cover so `undo()` can restore it exactly.

### 3. Singleton Pattern
**Where:** `minesweeper.model.HighScoreManager`
- `getInstance()` returns the single shared instance.
- Thread-safe via double-checked locking.
- **Why:** Both `Game` (writes score on win) and `BoardView` (reads scores)
  must share the same list without any cross-wiring.

### 4. Template Method Pattern (bonus)
**Where:** `minesweeper.model.Cell`
- `reveal()` is the template method: check-cover → set RevealedCover → `doReveal()`
- `doReveal()` is the hook: `MineCell` calls detonation; `SafeCell` triggers flood-fill.
- **Why:** Keeps the reveal protocol consistent in one place while letting
  each subclass define its unique behaviour.

## Package Structure

```
src/minesweeper/
├── Main.java                     ← entry point & game loop
├── model/
│   ├── Cell.java                 ← abstract (Template Method)
│   ├── MineCell.java
│   ├── SafeCell.java
│   ├── Cover.java                ← abstract
│   ├── PlainCover.java
│   ├── FlagCover.java
│   ├── QuestionCover.java
│   ├── RevealedCover.java
│   ├── Board.java
│   ├── Score.java
│   └── HighScoreManager.java     ← Singleton
├── patterns/
│   ├── Game.java                 ← State Context
│   ├── GameState.java            ← State interface
│   ├── GameStates.java           ← PlayingState, WonState, LostState
│   └── Timer.java
├── controller/
│   ├── Command.java              ← Command interface
│   ├── RevealCommand.java        ← + FlagCommand (in same file)
│   └── CommandManager.java       ← Invoker
└── view/
    └── BoardView.java            ← text renderer
```
