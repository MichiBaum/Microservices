package com.michibaum.chess_play_library

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class BoardIT {
    
    @Test
    fun `default board move one pawn`() {
        val board = Board.createDefault()
        val move = Move(Square(File.E, Rank.R2), Square(File.E, Rank.R4))
        val newBoard = board.move(move)
        assertEquals(Piece(Color.WHITE, PieceType.PAWN), newBoard.pieces[Square(File.E, Rank.R4)])
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", newBoard.toFen().toString())
    }

    @Test
    fun `default board italian game`() {
        val board = Board.createDefault()
            .move(Move(Square(File.E, Rank.R2), Square(File.E, Rank.R4)))
            .move(Move(Square(File.E, Rank.R7), Square(File.E, Rank.R5)))
            .move(Move(Square(File.G, Rank.R1), Square(File.F, Rank.R3)))
            .move(Move(Square(File.B, Rank.R8), Square(File.C, Rank.R6)))
            .move(Move(Square(File.F, Rank.R1), Square(File.C, Rank.R4)))
        
        assertEquals("r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 3 3", board.toFen().toString())
    }

    @Test
    fun `default board italian game with SAN`() {
        val board = Board.createDefault()
            .move("e4")
            .move("e5")
            .move("Nf3")
            .move("Nc6")
            .move("Bc4")
        assertEquals("r1bqkbnr/pppp1ppp/2n5/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R b KQkq - 3 3", board.toFen().toString())
    }

    @Test
    fun `default board and some captures with SAN`() {
        var board = Board.createDefault()
        board = board.move("e4")
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1", board.toFen().toString())
        board = board.move("d5")
        assertEquals("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2", board.toFen().toString())
        board = board.move("exd5")
        assertEquals("rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 2", board.toFen().toString())
        board = board.move("Qxd5")
        assertEquals("rnb1kbnr/ppp1pppp/8/3q4/8/8/PPPP1PPP/RNBQKBNR w KQkq - 0 3", board.toFen().toString())
    }
    
    @Test
    fun `castling with SAN`() {
        val board = Board.createDefault()
            .move("e4")
            .move("e5")
            .move("Nf3")
            .move("Nc6")
            .move("Bc4")
            .move("Nf6")
            .move("O-O")
        assertEquals("r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQ1RK1 b kq - 5 4", board.toFen().toString())
    }

    @Test
    fun `promotion with SAN`() {
        val board = Board.fromFen("8/P7/8/8/8/8/8/k6K w - - 0 1")
            .move("a8=Q")
        assertEquals("Q7/8/8/8/8/8/8/k6K b - - 0 1", board.toFen().toString())
    }

    @Test
    fun `disambiguation without x for capture should throw exception`() {
        val board = Board.fromFen("8/8/8/4p3/8/3N1N2/8/K6k w - - 0 1")
        assertThrows(IllegalMoveException::class.java) {
            board.move("Nde5")
        }
    }

    @Test
    fun `disambiguation capture with SAN`() {
        val board = Board.fromFen("8/8/8/4p3/8/3N1N2/8/K6k w - - 0 1")
            .move("Ndxe5")
        assertEquals("8/8/8/4N3/8/5N2/8/K6k b - - 0 1", board.toFen().toString())
    }

    @Test
    fun `ambiguous move should throw exception`() {
        val board = Board.fromFen("8/8/8/4p3/8/3N1N2/8/K6k w - - 0 1")
        assertThrows(AmbiguousMoveException::class.java) {
            board.move("Nxe5")
        }
    }

    @Test
    fun `played moves are saved on the board`() {
        val board = Board.createDefault()
            .move("e4")
            .move("e5")
            .move("Nf3")

        assertEquals(3, board.playedMoves.size)
        assertEquals(Square(File.E, Rank.R2), board.playedMoves[0].from)
        assertEquals(Square(File.E, Rank.R4), board.playedMoves[0].to)
        assertEquals(Square(File.E, Rank.R7), board.playedMoves[1].from)
        assertEquals(Square(File.E, Rank.R5), board.playedMoves[1].to)
        assertEquals(Square(File.G, Rank.R1), board.playedMoves[2].from)
        assertEquals(Square(File.F, Rank.R3), board.playedMoves[2].to)
    }

    @Test
    fun `history navigation works with list`() {
        val board = Board.createDefault()
            .move("e4")
            .move("e5")

        assertEquals(2, board.playedMoves.size)
        val firstMove = board.playedMoves[0]
        assertEquals(Square(File.E, Rank.R2), firstMove.from)

        val secondMove = board.playedMoves[1]
        assertEquals(Square(File.E, Rank.R7), secondMove.from)
    }

}