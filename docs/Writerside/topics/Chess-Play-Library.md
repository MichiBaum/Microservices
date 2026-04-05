# Chess Play Library

The **Chess Play Library** is a lightweight Kotlin-based library designed to handle chess game logic. 
It provides a robust set of tools for managing chess boards, piece movements, and standard notations.

## Key Features

- **Board Management**: Initialize a chess board with the standard starting position or any custom setup.
- **FEN Support**: Import and export game states using Forsyth-Edwards Notation (FEN).
- **Move Execution**: Perform moves using coordinate-based `Move` objects or directly via Standard Algebraic Notation (SAN) strings like `e4`, `Nf3`, or `O-O`.
- **Game Rules**: Includes logic for piece reachability, captures, castling, en passant, and pawn promotion.
- **State Tracking**: Automatically updates side to move, castling rights, en passant targets, halfmove clock, and fullmove number.
- **Move History**: Maintains a chronological list of all moves played on the board.

## Usage Examples

### Initializing a Board

```kotlin
val board = Board.createDefault()
// Or from a FEN string
val customBoard = Board.fromFen("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2")
```

### Performing Moves

```kotlin
// Using coordinates
val board = Board.createDefault()
    .move(Move(Square(File.E, Rank.R2), Square(File.E, Rank.R4)))

// Using SAN
val updatedBoard = board.move("Nf3")
    .move("e5")
    .move("Bc4")
```

### Exporting State

```kotlin
val currentFen = board.toFen()
// Access played moves
val history = board.playedMoves
```

## Architecture

The library is built with a clean separation of concerns:
- **`Board`**: The aggregate root representing the current game state and pieces.
- **`SanMapper`**: Handles the complexity of parsing human-readable notation into executable moves.
- **`ChessEngine`**: Cohesive engine for chess game logic, combining movement rules and board processing.
- **`FenMapper`**: Manages the conversion to and from the FEN string format.