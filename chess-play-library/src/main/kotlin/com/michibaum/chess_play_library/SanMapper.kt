package com.michibaum.chess_play_library

/**
 * Utility object for parsing SAN (Standard Algebraic Notation) strings into Move objects.
 */
object SanMapper {
    /**
     * Parses a SAN string for a given board and returns the corresponding Move object.
     * It uses the current board state to resolve piece movement and disambiguation.
     *
     * @param board The current board state.
     * @param san The SAN string (e.g., "e4", "Nf3", "exd5", "O-O").
     * @return The Move object.
     * @throws InvalidSanNotationException if the SAN string is invalid or no legal move matches it.
     * @throws AmbiguousMoveException if the SAN string is ambiguous.
     */
    fun fromSan(board: Board, san: String): Move {
        if (san == "O-O") {
            val rank = if (board.gameState.sideToMove == Color.WHITE) Rank.R1 else Rank.R8
            return Move(Square(File.E, rank), Square(File.G, rank))
        }
        if (san == "O-O-O") {
            val rank = if (board.gameState.sideToMove == Color.WHITE) Rank.R1 else Rank.R8
            return Move(Square(File.E, rank), Square(File.C, rank))
        }

        // Remove check/mate symbols
        val cleanedSan = san.replace("+", "").replace("#", "")
        
        // Promotion
        var promotion: PieceType? = null
        var mainSan = cleanedSan
        if (cleanedSan.contains("=")) {
            val parts = cleanedSan.split("=")
            mainSan = parts[0]
            promotion = PieceType.fromChar(parts[1][0])
        }

        // Capture
        val isCapture = mainSan.contains("x")
        val sanWithoutX = mainSan.replace("x", "")

        // Piece type
        var pieceType = PieceType.PAWN
        var movePart = sanWithoutX
        if (sanWithoutX.isNotEmpty() && sanWithoutX[0].isUpperCase()) {
            pieceType = PieceType.fromChar(sanWithoutX[0]) ?: PieceType.PAWN
            movePart = sanWithoutX.substring(1)
        }

        // Target square is the last two characters of the remaining notation part
        if (movePart.length < 2) throw InvalidSanNotationException(san)
        val targetSquareStr = movePart.takeLast(2)
        val targetSquare = Square.fromString(targetSquareStr) ?: throw InvalidSanNotationException(san)
        
        // Disambiguation: characters before the target square provide additional clues 
        // to identify the starting square (e.g., 'd' in "Nde5")
        val disambiguation = movePart.dropLast(2)
        var fromFile: File? = null
        var fromRank: Rank? = null
        for (char in disambiguation) {
            val f = File.fromChar(char)
            if (f != null && char.isLowerCase()) fromFile = f
            val r = Rank.fromChar(char)
            if (r != null) fromRank = r
        }

        // Find the piece: search for a piece of the correct type and side that 
        // can legally reach the target square, respecting any disambiguation.
        val candidates = board.pieces.filter { (square, piece) ->
            piece.pieceType == pieceType &&
            piece.side == board.gameState.sideToMove &&
            (fromFile == null || square.file == fromFile) &&
            (fromRank == null || square.rank == fromRank) &&
            ChessEngine.canReach(board, square, targetSquare, isCapture)
        }.keys

        if (candidates.isEmpty()) throw IllegalMoveException(san, "No legal move matches notation for board ${board.toFen()}")
        if (candidates.size > 1) {
            throw AmbiguousMoveException(san)
        }

        val fromSquare = candidates.first()
        return Move(fromSquare, targetSquare, promotion)
    }
}
