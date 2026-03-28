package com.michibaum.chess_play_library

/**
 * Represents a rank on a chess board (1-8).
 *
 * @property char The FEN character representation of the rank.
 */
enum class Rank(val char: Char) : ToFen {
    R1('1'),
    R2('2'),
    R3('3'),
    R4('4'),
    R5('5'),
    R6('6'),
    R7('7'),
    R8('8');

    override fun toFen(): FenString = FenString(char.toString())

    /**
     * Returns a Rank from its character representation.
     *
     * @param char The character to parse ('1'-'8').
     * @return The corresponding Rank or null if the character is invalid.
     */
    companion object {
        fun fromChar(char: Char): Rank? =
            values().find { it.char == char }
    }
}