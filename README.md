# SYSC 3110 Group 15 Scrabble Project

## Deliverable Breakdown
The current version, Milestone 1, is a text-based version of the popular game Scrabble. It is an MVP of the final deliverable and contains the core functionality such as, but not limited to:

- `Game.java`: This controls the main game loop, player turns, and keyboard input detection.
- `Board.java`: This creates the 15x15 game board, verifies placements, and displays the state of the tiles.
- `Player.java`: This represents the player and handles their tiles and score.
- `Tile.java`: This represents a single Scrabble tile with its letter and value.
- `TileBag.java`: This is responsible for drawing and replenishing tiles.
- `Dictionary.java`: This verifies and determines whether a word is acceptable by using a provided list of words.

The rest of the deliverables include:
- A UML and sequence diagram
- A compilable `.jar` file
- The Java source code

## Running Instructions

To run the program, open your terminal and enter:
```java -jar Scrabble.jar```

Or, if using IntelliJ, simply navigate to `Game.java` and press the Play button.

## Authors

- Abdullah Khan 101305235
- Adrian Joaquin 101226876
- Ismael Ouadria 101284947
- Rayyan Kashif 101274266

## Changes since previous deliverable

*N/A, first milestone submission. Will populate starting from Milestone 2.*

## Known Issues

*N/A*

## Next Steps

- **Milestone 2**: In this milestone, we will implement a GUI for our Milestone 1 deliverable in a JFrame with the input via the user's mouse. 
- **Milestone 3**: In this milestone, we will implement premium features into the Milestone 2 GUI such as blank tiles, premium squares, and multiple AI players.
- **Milestone 4**: In this milestone, we will add 3 additional features to our Milestone 3 deliverable: a multi-level undo/redo feature, a save/load feature, and custom boards with alternate placements of premium squares.