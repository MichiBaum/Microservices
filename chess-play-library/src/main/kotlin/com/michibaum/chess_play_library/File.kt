package com.michibaum.chess_play_library

/**
 * Represents a file on a chess board (a-h).
 *
 * @property char The FEN character representation of the file.
 */
enum class File(val char: Char) : ToFen {
    A('a'),
    B('b'),
    C('c'),
    D('d'),
    E('e'),
    F('f'),
    G('g'),
    H('h');

    override fun toFen(): FenString = FenString(char.toString())

    /**
     * Returns a File from its character representation.
     *
     * @param char The character to parse (e.g., 'a', 'b', etc.).
     * @return The corresponding File or null if the character is invalid.
     */
    companion object {
        fun fromChar(char: Char): File? =
            values().find { it.char == char.lowercaseChar() }
    }
}