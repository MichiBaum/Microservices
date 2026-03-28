package com.michibaum.chess_play_library

/**
 * Represents a move of a piece from one square to another.
 *
 * @property from The starting square of the move.
 * @property to The destination square of the move.
 * @property promotion The piece type to promote to, if any (only applicable for pawn moves to the last rank).
 */
data class Move(
    val from: Square,
    val to: Square,
    val promotion: PieceType? = null
)
