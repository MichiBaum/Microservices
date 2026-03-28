package com.michibaum.chess_play_library

/**
 * Utility object for converting between a Board instance and its FEN representation.
 */
object FenMapper {
    /** The standard FEN for a starting position. */
    const val STARTING_FEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"

    /**
     * Converts a board instance to its full FEN representation.
     *
     * @param board The board instance.
     * @return The FEN string wrapper.
     */
    fun toFen(board: Board): FenString {
        val fen = StringBuilder()

        // 1. Piece placement
        for (rank in Rank.values().reversed()) {
            var emptySquares = 0
            for (file in File.values()) {
                val square = Square(file, rank)
                val piece = board.pieces[square]
                if (piece == null) {
                    emptySquares++
                } else {
                    if (emptySquares > 0) {
                        fen.append(emptySquares)
                        emptySquares = 0
                    }
                    fen.append(piece.toFen().value)
                }
            }
            if (emptySquares > 0) {
                fen.append(emptySquares)
            }
            if (rank != Rank.R1) {
                fen.append("/")
            }
        }

        fen.append(" ")
        fen.append(board.gameState.toFen().value)

        return FenString(fen.toString())
    }

    /**
     * Creates a Board instance from its FEN string representation.
     *
     * @param fen The FEN string wrapper.
     * @return A Board instance representing the parsed state.
     * @throws InvalidFenException if the FEN string is invalid.
     */
    fun fromFen(fen: FenString): Board {
        val fenValue = fen.value
        val parts = fenValue.split(" ")
        if (parts.size < 4) throw InvalidFenException(fenValue)

        val piecePlacement = parts[0]
        val activeColor = parts.getOrElse(1) { "w" }
        val castling = parts.getOrElse(2) { "KQkq" }
        val enPassant = parts.getOrElse(3) { "-" }
        val halfmove = parts.getOrElse(4) { "0" }.toIntOrNull() ?: throw InvalidFenException(fenValue)
        val fullmove = parts.getOrElse(5) { "1" }.toIntOrNull() ?: throw InvalidFenException(fenValue)

        val pieces = mutableMapOf<Square, Piece>()
        val ranks = piecePlacement.split("/")
        for ((rankIdx, rankStr) in ranks.withIndex()) {
            val rank = Rank.values()[7 - rankIdx]
            var fileIdx = 0
            for (char in rankStr) {
                if (char.isDigit()) {
                    fileIdx += char.toString().toInt()
                } else {
                    val side = if (char.isUpperCase()) Color.WHITE else Color.BLACK
                    val pieceType = PieceType.fromChar(char) ?: continue
                    val file = File.values()[fileIdx]
                    pieces[Square(file, rank)] = Piece(side, pieceType)
                    fileIdx++
                }
            }
        }

        val gameState = GameState(
            sideToMove = if (activeColor == "w") Color.WHITE else Color.BLACK,
            castlingAbility = CastlingAbility.fromFen(castling),
            enPassantSquare = if (enPassant == "-") null else Square.fromString(enPassant),
            halfmoveClock = halfmove,
            fullmoveNumber = fullmove
        )

        return Board(
            pieces = pieces,
            gameState = gameState
        )
    }
}
