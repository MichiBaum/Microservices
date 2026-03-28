package com.michibaum.chess_play_library

/**
 * Represents a square on a chess board.
 *
 * @property file The file (column) of the square.
 * @property rank The rank (row) of the square.
 */
data class Square(
    val file: File,
    val rank: Rank
) : ToFen {

    override fun toFen(): FenString {
        return FenString("${file.char}${rank.char}")
    }

    /**
     * Returns a Square from its string representation (e.g., "e4").
     *
     * @param square The string to parse.
     * @return The corresponding Square or null if the string is invalid.
     */
    companion object {
        fun fromString(square: String): Square? {
            if (square.length != 2) return null
            val file = File.fromChar(square[0]) ?: return null
            val rank = Rank.fromChar(square[1]) ?: return null
            return Square(file, rank)
        }
    }
}