package com.michibaum.chess_play_library

/**
 * Represents the type of a chess piece (e.g., Pawn, Knight, etc.).
 *
 * @property fenNotation The uppercase character used in FEN to represent the piece.
 */
enum class PieceType(val fenNotation: String) : ToFen {
    PAWN("P"),
    KNIGHT("N"),
    BISHOP("B"),
    ROOK("R"),
    QUEEN("Q"),
    KING("K");

    override fun toFen(): FenString = FenString(fenNotation)

    /**
     * Returns the FEN character representation for this piece type, colored based on the side.
     * White pieces are uppercase, black pieces are lowercase.
     *
     * @param side The side the piece belongs to.
     * @return The colored FEN string.
     */
    fun fenColored(side: Color): String {
        return if (side == Color.WHITE)
            fenNotation.uppercase()
        else
            fenNotation.lowercase()
    }

    /**
     * Returns a PieceType from its character representation.
     *
     * @param char The character to parse (case-insensitive).
     * @return The corresponding PieceType or null if the character is invalid.
     */
    companion object {
        fun fromChar(char: Char): PieceType? =
            values().find { it.fenNotation.equals(char.toString(), ignoreCase = true) }
    }
}