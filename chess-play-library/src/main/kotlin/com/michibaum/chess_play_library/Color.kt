package com.michibaum.chess_play_library

/**
 * Represents a side in a chess game.
 */
enum class Color : ToFen {
    WHITE,
    BLACK;

    override fun toFen(): FenString = FenString(if (this == WHITE) "w" else "b")
}