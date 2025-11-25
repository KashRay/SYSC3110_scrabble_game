# SYSC 3110 Group 15 Scrabble Project

## Deliverable Breakdown
Milestone 3 extends our full MVC-based Scrabble implementation by adding **blank tiles**, **premium squares**, **complete AI gameplay**, and **comprehensive JUnit testing**.

This milestone includes:
- **Model:** (`Game`, `Board`, `Player`, `AIPlayer`, `Tile`, `TileBag`, `Dictionary`) supporting premium squares, blank tiles, scoring rules, AI turns, and move validation.
- **View:** (`App`) Swing-based graphical interface for board interaction, tile selection, scoring display, and turn updates.
- **Controller:** (`ScrabbleController`) event-driven communication between UI and game logic.
- **Blank Tiles:** Score 0; player/AI assigns a letter when played.
- **Premium Squares:** Full 15×15 Scrabble layout (DL, TL, DW, TW) with correct word/letter multiplier logic.
- **AI Player:** Generates and evaluates all legal moves, choosing the **highest-scoring valid move** based on full board simulation.
- **Dictionary:** File-based loading and validation of all words used by the player and AI.
- **Testing:** JUnit tests covering move validation, scoring, dictionary loading, blank tile rules, premium squares, and AI legality.
- **Documentation:** UML class diagram and sequence diagrams fully updated to reflect Milestone 3 behaviour.

Deliverables:
- Updated UML + sequence diagrams
- `.jar` executable
- Source code (Model, View, Controller, AI)
- JUnit test suite
- `README.md`

## Running Instructions

To run the program, open a terminal and enter:
```bash
java -jar Scrabble.jar
```
## Authors
- Abdullah Khan    101305235
- Adrian Joaquin   101226876
- Ismael Ouadria   101284947
- Rayyan Kashif    101274266

## Changes Since Previous Deliverable
- Implemented blank tiles with user/AI letter assignment.
- Added complete premium square scoring system.
- Added AI player with full-board word simulation and scoring evaluation.
- Refactored `Game` logic for AI integration and premium scoring.
- Added JUnit tests for scoring, blanks, validation, and AI behaviour.
- Updated all diagrams to reflect AI logic and new rules.

## Known Issues
- Minor UI scaling differences on high-DPI screens.
- AI computation time increases with very large dictionaries.

## Next Steps
- **Milestone 4:** Implement multi-level undo/redo, save/load, and custom board configuration.

## AI Player Strategy
The AI evaluates every dictionary word at every board position, horizontally and vertically.  
For each candidate:

1. Check boundary fit.
2. Check legality via `Board.isValidPlacement(...)`.
3. Simulate tile placement.
4. Score using `Game.analyzeMove(...)` (includes DL/TL/DW/TW and blank tiles).
5. Track the highest-scoring legal move.