# SYSC 3110 Group 15 Scrabble Project

## Deliverable Breakdown
The current version, **Milestone 2**, extends the text-based MVP from Milestone 1 by implementing a fully functional **graphical user interface (GUI)** and refactoring the project to follow the **Model-View-Controller (MVC)** architecture.

This milestone includes:
- **Graphical Interface (View):**  
  Implemented using `JFrame` and `Swing` components (`App.java`). Players interact with the game entirely through mouse input.
- **Controller:**  
  Implemented in `ScrabbleController.java`, handling all user actions and communication between the model and the view through Java’s event model.
- **Model:**  
  Core logic from Milestone 1 (`Game.java`, `Board.java`, `Player.java`, `Tile.java`, `TileBag.java`, `Dictionary.java`) refactored to integrate with the MVC structure.
- **Dictionary:**  
  Updated to load words from a file and perform real-time validation of placed words.
- **UML and Sequence Diagrams:**  
  Updated to include all classes, full method and variable signatures, and interactions between the Model, View, and Controller.
- **JUnit Testing:**  
  Model classes now include comprehensive JUnit tests for word placement, scoring logic, and dictionary validation.

Deliverables included in this submission:
- Updated UML and sequence diagrams
- Compilable `.jar` file
- Java source code (Model, View, Controller)
- JUnit test suite for model components
- `README.md` documentation

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
- Added `App.java` to implement a graphical interface using Swing.
- Introduced `ScrabbleController.java` to handle event-driven user interaction.
- Refactored `Game.java` and related model classes to support MVC separation.
- Added event-based communication between the View and Model.
- Implemented JUnit tests for all model logic.
- Updated UML class and sequence diagrams to reflect MVC structure and new relationships.

## Known Issues
- Minor layout scaling differences may occur on high-DPI displays.
- No premium tile or blank tile functionality yet (planned for Milestone 3).

## Next Steps
- **Milestone 3:** Add premium tiles, blank tiles, and AI player functionality.
- **Milestone 4:** Implement multi-level undo/redo, save/load, and custom board configuration.