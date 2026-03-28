package com.michibaum.chess_play_library

/**
 * Represents the castling rights of both players.
 *
 * @property whiteKingside Whether white can castle kingside.
 * @property whiteQueenside Whether white can castle queenside.
 * @property blackKingside Whether black can castle kingside.
 * @property blackQueenside Whether black can castle queenside.
 */
data class CastlingAbility(
    val whiteKingside: Boolean = true,
    val whiteQueenside: Boolean = true,
    val blackKingside: Boolean = true,
    val blackQueenside: Boolean = true
) : ToFen {

    companion object {
        /**
         * Parses castling rights from its FEN string representation (e.g., "KQkq" or "-").
         *
         * @param s The FEN castling field string.
         * @return A CastlingAbility instance representing the rights.
         */
        fun fromFen(s: String): CastlingAbility {
            if (s == "-") return CastlingAbility(false, false, false, false)
            return CastlingAbility(
                whiteKingside = s.contains('K'),
                whiteQueenside = s.contains('Q'),
                blackKingside = s.contains('k'),
                blackQueenside = s.contains('q')
            )
        }
    }

    /**
     * Updates castling rights based on a move and the pieces involved.
     * Rights are lost if a king or rook moves, or if a rook is captured.
     *
     * @param move The move performed.
     * @param piece The piece that was moved.
     * @param targetPiece The piece that was captured, if any.
     * @return A new CastlingAbility instance with updated rights.
     */
    fun updateAfterMove(move: Move, piece: Piece, targetPiece: Piece?): CastlingAbility {
        var wk = whiteKingside
        var wq = whiteQueenside
        var bk = blackKingside
        var bq = blackQueenside

        // If king moves
        if (piece.pieceType == PieceType.KING) {
            if (piece.side == Color.WHITE) {
                wk = false
                wq = false
            } else {
                bk = false
                bq = false
            }
        }

        // If rook moves from its starting position
        if (piece.pieceType == PieceType.ROOK) {
            if (piece.side == Color.WHITE) {
                if (move.from == Square(File.H, Rank.R1)) wk = false
                if (move.from == Square(File.A, Rank.R1)) wq = false
            } else {
                if (move.from == Square(File.H, Rank.R8)) bk = false
                if (move.from == Square(File.A, Rank.R8)) bq = false
            }
        }

        // If rook is captured at its starting position
        if (targetPiece?.pieceType == PieceType.ROOK) {
            if (targetPiece.side == Color.BLACK) {
                if (move.to == Square(File.H, Rank.R8)) bk = false
                if (move.to == Square(File.A, Rank.R8)) bq = false
            } else {
                if (move.to == Square(File.H, Rank.R1)) wk = false
                if (move.to == Square(File.A, Rank.R1)) wq = false
            }
        }

        return CastlingAbility(wk, wq, bk, bq)
    }

    override fun toFen(): FenString {
        val sb = StringBuilder()
        if (whiteKingside) sb.append('K')
        if (whiteQueenside) sb.append('Q')
        if (blackKingside) sb.append('k')
        if (blackQueenside) sb.append('q')
        return FenString(if (sb.isEmpty()) "-" else sb.toString())
    }
}
