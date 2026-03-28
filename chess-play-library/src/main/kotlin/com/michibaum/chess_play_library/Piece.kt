package com.michibaum.chess_play_library

/**
 * Represents a specific chess piece on the board.
 *
 * @property side The side the piece belongs to (White or Black).
 * @property pieceType The type of the piece (Pawn, Rook, etc.).
 */
data class Piece(val side: Color, val pieceType: PieceType) : ToFen {
    override fun toFen(): FenString = FenString(pieceType.fenColored(side))
}
