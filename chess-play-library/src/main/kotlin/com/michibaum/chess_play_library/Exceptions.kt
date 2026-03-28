package com.michibaum.chess_play_library

/**
 * Base exception for all chess-related errors.
 */
open class ChessException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/**
 * Thrown when a SAN (Standard Algebraic Notation) string cannot be parsed.
 */
class InvalidSanNotationException(san: String, boardFen: String? = null) : 
    ChessException("Invalid SAN notation: $san" + (if (boardFen != null) " for board $boardFen" else ""))

/**
 * Thrown when a move is illegal in the current board state.
 */
class IllegalMoveException(move: String, reason: String) : 
    ChessException("Illegal move $move: $reason")

/**
 * Thrown when a move is ambiguous (more than one piece can reach the target square).
 */
class AmbiguousMoveException(san: String) : 
    ChessException("Ambiguous move: $san")

/**
 * Thrown when a FEN string is invalid.
 */
class InvalidFenException(fen: String) : 
    ChessException("Invalid FEN: $fen")
