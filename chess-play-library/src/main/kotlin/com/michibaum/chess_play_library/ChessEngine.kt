package com.michibaum.chess_play_library

import kotlin.math.abs

/**
 * Cohesive engine for chess game logic, combining movement rules and board processing.
 */
object ChessEngine {

    /**
     * Executes the given move on the board and returns a new board state.
     *
     * @param board The current board.
     * @param move The move to perform.
     * @return A new Board instance with the move applied.
     * @throws IllegalMoveException if the move is invalid.
     */
    fun move(board: Board, move: Move): Board {
        val pieces = board.pieces.toMutableMap()
        val piece = pieces.remove(move.from) 
            ?: throw IllegalMoveException(move.toString(), "No piece at starting square ${move.from}")
        
        val targetPiece = pieces[move.to]

        // 1. Handle en passant capture
        if (piece.pieceType == PieceType.PAWN && move.to == board.gameState.enPassantSquare && targetPiece == null) {
            val capturedPawnSquare = Square(move.to.file, move.from.rank)
            pieces.remove(capturedPawnSquare)
        }

        // 2. Handle castling move side-effects
        if (piece.pieceType == PieceType.KING && abs(move.from.file.ordinal - move.to.file.ordinal) == 2) {
            val (rookFrom, rookTo) = when (move.to.file) {
                File.G -> Square(File.H, move.to.rank) to Square(File.F, move.to.rank)
                File.C -> Square(File.A, move.to.rank) to Square(File.D, move.to.rank)
                else -> throw IllegalMoveException(move.toString(), "Invalid castling move")
            }
            val rook = pieces.remove(rookFrom)
            if (rook != null) {
                pieces[rookTo] = rook
            }
        }

        // Update halfmove clock
        val newHalfmoveClock = if (piece.pieceType == PieceType.PAWN || targetPiece != null) {
            0
        } else {
            board.gameState.halfmoveClock + 1
        }

        // Update castling ability
        val newCastlingAbility = board.gameState.castlingAbility.updateAfterMove(move, piece, targetPiece)

        // Handle piece movement (including promotion)
        pieces[move.to] = if (move.promotion != null) {
            Piece(piece.side, move.promotion)
        } else {
            piece
        }

        // Set en passant square
        val newEnPassantSquare = if (piece.pieceType == PieceType.PAWN && abs(move.from.rank.ordinal - move.to.rank.ordinal) == 2) {
            val enPassantRank = if (piece.side == Color.WHITE) Rank.R3 else Rank.R6
            Square(move.from.file, enPassantRank)
        } else {
            null
        }

        // Switch side to move
        val nextSideToMove = if (board.gameState.sideToMove == Color.WHITE) Color.BLACK else Color.WHITE

        // Update fullmove number
        val newFullmoveNumber = if (nextSideToMove == Color.WHITE) {
            board.gameState.fullmoveNumber + 1
        } else {
            board.gameState.fullmoveNumber
        }

        return Board(
            pieces = pieces,
            gameState = board.gameState.copy(
                sideToMove = nextSideToMove,
                castlingAbility = newCastlingAbility,
                enPassantSquare = newEnPassantSquare,
                halfmoveClock = newHalfmoveClock,
                fullmoveNumber = newFullmoveNumber
            ),
            playedMoves = board.playedMoves + move
        )
    }

    /**
     * Determines whether a piece on a given square can reach a target square.
     */
    fun canReach(board: Board, from: Square, to: Square, isCapture: Boolean): Boolean {
        val piece = board.pieces[from] ?: return false
        val targetPiece = board.pieces[to]

        if (targetPiece?.side == piece.side) return false

        if (isCapture && targetPiece == null && (piece.pieceType != PieceType.PAWN || to != board.gameState.enPassantSquare)) {
            return false
        }
        if (!isCapture && targetPiece != null) {
            return false
        }

        return when (piece.pieceType) {
            PieceType.PAWN -> canPawnReach(board, from, to, piece.side, isCapture)
            PieceType.KNIGHT -> canKnightReach(from, to)
            PieceType.BISHOP -> canBishopReach(board, from, to)
            PieceType.ROOK -> canRookReach(board, from, to)
            PieceType.QUEEN -> canQueenReach(board, from, to)
            PieceType.KING -> canKingReach(from, to)
        }
    }

    private fun canPawnReach(board: Board, from: Square, to: Square, side: Color, isCapture: Boolean): Boolean {
        val df = to.file.ordinal - from.file.ordinal
        val dr = to.rank.ordinal - from.rank.ordinal

        if (side == Color.WHITE) {
            if (isCapture) {
                return dr == 1 && abs(df) == 1
            } else {
                if (df != 0) return false
                if (dr == 1) return true
                if (dr == 2 && from.rank == Rank.R2) {
                    val intermediate = Square(from.file, Rank.R3)
                    return board.pieces[intermediate] == null
                }
            }
        } else {
            if (isCapture) {
                return dr == -1 && abs(df) == 1
            } else {
                if (df != 0) return false
                if (dr == -1) return true
                if (dr == -2 && from.rank == Rank.R7) {
                    val intermediate = Square(from.file, Rank.R6)
                    return board.pieces[intermediate] == null
                }
            }
        }
        return false
    }

    private fun canKnightReach(from: Square, to: Square): Boolean {
        val df = abs(to.file.ordinal - from.file.ordinal)
        val dr = abs(to.rank.ordinal - from.rank.ordinal)
        return (df == 1 && dr == 2) || (df == 2 && dr == 1)
    }

    private fun canBishopReach(board: Board, from: Square, to: Square): Boolean {
        val df = to.file.ordinal - from.file.ordinal
        val dr = to.rank.ordinal - from.rank.ordinal
        if (abs(df) != abs(dr) || df == 0) return false
        return isPathClear(board, from, to)
    }

    private fun canRookReach(board: Board, from: Square, to: Square): Boolean {
        val df = to.file.ordinal - from.file.ordinal
        val dr = to.rank.ordinal - from.rank.ordinal
        if ((df != 0 && dr != 0) || (df == 0 && dr == 0)) return false
        return isPathClear(board, from, to)
    }

    private fun canQueenReach(board: Board, from: Square, to: Square): Boolean {
        return canBishopReach(board, from, to) || canRookReach(board, from, to)
    }

    private fun canKingReach(from: Square, to: Square): Boolean {
        val df = abs(to.file.ordinal - from.file.ordinal)
        val dr = abs(to.rank.ordinal - from.rank.ordinal)
        return df <= 1 && dr <= 1 && (df != 0 || dr != 0)
    }

    private fun isPathClear(board: Board, from: Square, to: Square): Boolean {
        val df = to.file.ordinal - from.file.ordinal
        val dr = to.rank.ordinal - from.rank.ordinal

        val sf = if (df > 0) 1 else if (df < 0) -1 else 0
        val sr = if (dr > 0) 1 else if (dr < 0) -1 else 0

        var currentFile = from.file.ordinal + sf
        var currentRank = from.rank.ordinal + sr

        while (currentFile != to.file.ordinal || currentRank != to.rank.ordinal) {
            val square = Square(File.values()[currentFile], Rank.values()[currentRank])
            if (board.pieces.containsKey(square)) return false
            currentFile += sf
            currentRank += sr
        }
        return true
    }
}
