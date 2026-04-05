package com.michibaum.chess_play_library

/**
 * Type-safe wrapper for a FEN (Forsyth-Edwards Notation) string.
 */
@JvmInline
value class FenString(val value: String) {
    override fun toString(): String = value
}
