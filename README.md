# SYSC 3110 Group 15 Scrabble Project

## Deliverable Breakdown
This milestone introduces multi-level undo/redo, full game serialization/deserialization, and custom board importing to our complete MVC Scrabble implementation.

This milestone also includes:

### Model
`Game`, `Board`, `Player`, `AIPlayer`, `Tile`, `TileBag`, `Dictionary`, `Move`
- Full Scrabble rule enforcement
- Premium squares (DL, TL, DW, TW)
- Blank tile support
- AI word generation and scoring
- Multi-level undo/redo using serialized snapshots of the `Game` object
- Save/load functionality through Java Serialization
- Custom board loading via XML files
- Updated equality checks to ensure structural consistency after serialization

### View
`App`
- Swing-based GUI for full board interaction
- Dynamic square colouring (premium, placed, validated)
- Player hand display
- Scoreboard and tile-bag tracking
- Buttons for Undo, Redo, Save, Load, Exchange/Pass, and Custom Board Import

### Controller
`ScrabbleController`
- Event-driven mapping of UI inputs to model actions
- State preservation before moves for undo/redo
- Integration of save/load dialogs
- Custom board import handling

### Undo / Redo System
- Implemented using two stacks of serialized `Game` objects
- Supports multiple consecutive undo/redo operations
- View buttons enable/disable automatically based on stack state

### Save / Load System
- Serializes only model components (views excluded)
- Dictionary automatically reloaded after deserialization
- Ensures full restoration of board, player hands, scores, tile bag, and turn state

### Custom Boards
- Supports XML-based premium tile layouts
- Users can load alternative Scrabble board configurations at runtime

### AI Player
- Evaluates all possible placements of all dictionary words
- Simulates tile usage (including blanks) and calls `Game.analyzeMove(...)`
- Chooses the highest-scoring valid move

### Dictionary
- File-based loading of wordlist (unchanged from previous milestone)
- Used by both human and AI players

### Testing
- Undo/redo stack logic
- Serialization/deserialization correctness
- Custom board import integrity

### Documentation Deliverables
- Updated UML class diagram (reflecting serialization, undo/redo, new methods)
- Sequence diagrams for Undo and Load Game
- Explanation of data structure updates
- `.jar` executable
- Source code
- JUnit test suite
- `README.md`

---

## Running Instructions

To run the program, open a terminal and enter:

```bash  
java -jar Scrabble.jar  
```

---

## Authors
- Abdullah Khan    101305235
- Adrian Joaquin   101226876
- Ismael Ouadria   101284947
- Rayyan Kashif    101274266

---

## Changes Since Previous Deliverable

### New Features
- Implemented multi-level undo/redo using serialized game snapshots
- Added Save Game and Load Game functionality
- Added Custom Board Import (XML-based premium tile layouts)

### Model Enhancements
- Added `undoStack` and `redoStack` to `Game`
- Added serialization methods:
    - `saveGame(File)`
    - `static loadGame(File)`
    - `storeState(Stack<byte[]>)`
    - `undo() / redo()`
- Rebuilt `equals(...)` methods for consistent state comparison after loading
- Added board import support to `Board`

### Controller Updates
- Added command handling for Save, Load, Undo, Redo, Import Board
- Ensured redo stack is cleared appropriately after player actions

### View Updates
- Added Undo/Redo buttons and logic
- Added Save/Load menu items
- Added custom board import option
- Added full visual refresh after undo/redo/load

### Testing
- Added JUnit tests for undo/redo correctness
- Added JUnit tests for serialization integrity
- Added JUnit tests for custom board XML import

---

## Known Issues
- Very large dictionaries may still slow down AI move generation
- XML files must be properly formatted; no schema validation included

---

## AI Player Strategy
The AI evaluates every dictionary word across all board positions in both orientations.  
For each candidate word:

1. Check boundaries using `Board.isValidPlacement(...)`
2. Verify tile availability (including blank substitution)
3. Simulate placement
4. Score using `Game.analyzeMove(...)`
5. Select the highest scoring legal move

---

## Next Steps
N/A