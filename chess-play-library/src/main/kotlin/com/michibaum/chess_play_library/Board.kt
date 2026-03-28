package com.michibaum.chess_play_library

/**
 * Represents a chess board including all pieces and the current game state.
 *
 * @property pieces A map containing all pieces and their current squares.
 * @property gameState The current state of the game (turn, castling rights, etc.).
 * @property playedMoves The history of all moves that have been played on this board.
 */
data class Board(
    val pieces: Map<Square, Piece> = emptyMap(),
    val gameState: GameState = GameState(),
    val playedMoves: List<Move> = emptyList()
) : ToFen {

    /**
     * Move a piece on the board based on a Move object.
     * Updates the board state, game state, and handles special moves like castling and en passant.
     *
     * @param move The move to perform.
     * @return A new Board instance with the move applied.
     */
    fun move(move: Move): Board =
        ChessEngine.move(this, move)

    /**
     * Move a piece on the board based on a SAN (Standard Algebraic Notation) string.
     * The move is parsed, validated against the current board state, and then executed.
     *
     * @param san The SAN string (e.g., "e4", "Nf3", "exd5").
     * @return A new Board instance with the move applied.
     * @throws InvalidSanNotationException if the SAN string is invalid or no legal move matches it.
     * @throws AmbiguousMoveException if the SAN string is ambiguous.
     */
    fun move(san: String): Board {
        val move = SanMapper.fromSan(this, san)
        return move(move)
    }

    /**
     * Converts the board instance to its FEN representation.
     *
     * @return The FEN string representation.
     */
    override fun toFen(): FenString = FenMapper.toFen(this)

    companion object {
        /**
         * Creates a new board with the standard chess starting position.
         *
         * @return A new Board instance.
         */
        fun createDefault(): Board = fromFen(FenMapper.STARTING_FEN)

        /**
         * Creates a board from a FEN (Forsyth-Edwards Notation) string representation.
         *
         * @param fen The FEN string wrapper.
         * @return A Board instance representing the given state.
         * @throws InvalidFenException if the FEN string is invalid.
         */
        fun fromFen(fen: FenString): Board = FenMapper.fromFen(fen)

        /**
         * Creates a board from a FEN string.
         *
         * @param fen The FEN string.
         * @return A Board instance representing the given state.
         * @throws InvalidFenException if the FEN string is invalid.
         */
        fun fromFen(fen: String): Board = fromFen(FenString(fen))
    }
}