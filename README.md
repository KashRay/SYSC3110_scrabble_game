# SYSC 3110 Group 15 Scrabble Project

## Deliverable Breakdown
This milestone adds the **Game Statistics Display** bonus feature (Feature 3) on top of the complete Milestone 4 implementation.



## Milestone 5 Bonus Feature: Game Statistics Display

### Gameplay Explanation
At the end of the game, a detailed statistics report is shown in a scrollable popup.  
The report displays:
- The winning player
- Total points scored by each player
- Number of turns taken
- All words played by each player along with their scores

### Where It Is Implemented
- `Game.endGame()`: Builds and displays the statistics summary
- `Player.addMove(PlayerMove)`: Records each played word
- `Player.getRecordedMoves()`: Provides move histories
- `Player.getTurnsTaken()`: Tracks turn counts
- `PlayerMove`: Stores `(score, word)` pairs

### Contributors
All team members contributed to testing, verifying formatting, and integrating the statistics system.

---

## Model
`Game`, `Board`, `Player`, `AIPlayer`, `Tile`, `TileBag`, `Dictionary`, `PlayerMove`
- Scrabble rule enforcement
- Premium squares and blank tile support
- AI scoring and placement
- Undo/redo via serialized snapshots
- Save/load functionality
- XML custom board import
- End-of-game statistics tracking

---

## View
`App`
- Swing-based interactive board
- Premium colouring, placement colouring, and validated tile colouring
- Player hand and score displays
- Undo, Redo, Save, Load, Exchange/Pass, Import buttons
- End-of-game popup for full statistics

---

## Controller
`ScrabbleController`
- Handles UI events and dispatches actions to the model
- Controls undo/redo snapshot creation
- Manages Save/Load dialogs
- Integrates statistics display via `Game.endGame()`

---

## Undo / Redo System
- Two-stack architecture storing serialized `Game` snapshots
- Supports multi-level undo/redo
- GUI buttons dynamically enabled/disabled

---

## Save / Load System
- Serializes only model components
- Dictionary reloaded after deserialization
- Restores all gameplay state

---

## Custom Boards
- Accepts XML premium tile layouts
- Users may import alternate Scrabble board designs

---

## AI Player
- Brute-force evaluation of all words across all positions
- Blank handling
- Uses `Game.analyzeMove(...)` for scoring

---

## Dictionary
- Loads from wordlist file
- Used by both human and AI logic

---

## Testing
JUnit coverage includes:
- Undo/redo behavior
- Serialization/deserialization
- Custom board import
- End-of-game statistics accuracy

---

## Documentation Deliverables
- UML class diagram
- Sequence diagrams (Undo, Load)
- Explanation of data structure decisions
- JAR executable
- Source code
- JUnit tests
- README

---

## Running Instructions

Before running the JAR file, complete the following steps:

- In the folder where you downloaded the JAR file, create a folder named `src`.
- Download the `wordlist.txt` file from the project repository (or use your own list of words).
- Place `wordlist.txt` inside the `src` folder you created.

To run the game:

Windows:
- Open Windows PowerShell and navigate to the directory containing your JAR file.

Linux:
- Open a terminal and navigate to the directory containing your JAR file.

Run the following command:

```bash
java -jar SYSC3110_scrabble_game.jar
```

(Note: If the JAR file was renamed, use the correct filename.)  
The game window should now launch.

---

## Authors
- Abdullah Khan    101305235
- Adrian Joaquin   101226876
- Ismael Ouadria   101284947
- Rayyan Kashif    101274266

---

## Changes Since Previous Deliverable

### New Features
- Added end game statistics display popup containing:
  - Winner
  - Final scores
  - Turns taken
  - Words played with individual word scores

### Model Enhancements
- Implemented statistics gathering using existing Player move-tracking
- Expanded `endGame()` to compile and present full game statistics

### Controller Updates
- Ensured end-game flow correctly triggers statistics window

### View Updates
- Added scrollable text window for post-game statistics

### Testing
- Added unit tests verifying statistics formatting and correctness

---

## Known Issues
- Very large dictionaries may slow AI generation
- XML custom boards require correct formatting

---

## AI Player Strategy
The AI evaluates every dictionary word across all possible positions and directions:

1. Validate bounds
2. Check tile availability (including blanks)
3. Simulate placement
4. Score using `Game.analyzeMove(...)`
5. Pick the highest scoring valid move

---

## Next Steps
N/A