package com.michibaum.chess_play_library

/**
 * Represents the current state of a chess game beyond the position of pieces.
 *
 * @property sideToMove The side whose turn it is to move.
 * @property castlingAbility The current castling rights for both players.
 * @property enPassantSquare The target square for an en passant capture, if applicable.
 * @property halfmoveClock The number of halfmoves since the last pawn move or capture (used for the 50-move rule).
 * @property fullmoveNumber The current fullmove number, incremented after each black move.
 */
data class GameState(
    val sideToMove: Color = Color.WHITE,
    val castlingAbility: CastlingAbility = CastlingAbility(),
    val enPassantSquare: Square? = null,
    val halfmoveClock: Int = 0,
    val fullmoveNumber: Int = 1
) : ToFen {
    override fun toFen(): FenString {
        return FenString("${sideToMove.toFen().value} ${castlingAbility.toFen().value} ${enPassantSquare?.toFen()?.value ?: "-"} $halfmoveClock $fullmoveNumber")
    }
}
