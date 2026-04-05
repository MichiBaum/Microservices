package com.michibaum.chess_play_library

/**
 * Interface for objects that can be converted to a FEN string representation.
 */
interface ToFen {
    /**
     * Converts the object to its FEN string representation.
     *
     * @return The FEN string wrapper.
     */
    fun toFen(): FenString
}