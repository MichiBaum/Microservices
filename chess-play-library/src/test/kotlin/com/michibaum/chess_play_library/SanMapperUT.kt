package com.michibaum.chess_play_library

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class SanMapperUT {

    @Test
    fun `test pawn move`() {
        val board = Board.createDefault()
        val move = SanMapper.fromSan(board, "e4")
        assertEquals(Square(File.E, Rank.R2), move.from)
        assertEquals(Square(File.E, Rank.R4), move.to)
        assertNull(move.promotion)
    }

    @Test
    fun `test piece move`() {
        val board = Board.createDefault()
        val move = SanMapper.fromSan(board, "Nf3")
        assertEquals(Square(File.G, Rank.R1), move.from)
        assertEquals(Square(File.F, Rank.R3), move.to)
    }

    @Test
    fun `test pawn capture`() {
        // e4 d5; exd5
        val board = Board.fromFen("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2")
        val move = SanMapper.fromSan(board, "exd5")
        assertEquals(Square(File.E, Rank.R4), move.from)
        assertEquals(Square(File.D, Rank.R5), move.to)
    }

    @Test
    fun `test piece capture`() {
        // e4 d5; exd5 Qxd5
        val board = Board.fromFen("rnbqkbnr/ppp1pppp/8/3P4/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 2")
        val move = SanMapper.fromSan(board, "Qxd5")
        assertEquals(Square(File.D, Rank.R8), move.from)
        assertEquals(Square(File.D, Rank.R5), move.to)
    }

    @Test
    fun `test kingside castling white`() {
        val board = Board.fromFen("r1bqkb1r/pppp1ppp/2n2n2/4p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4")
        val move = SanMapper.fromSan(board, "O-O")
        assertEquals(Square(File.E, Rank.R1), move.from)
        assertEquals(Square(File.G, Rank.R1), move.to)
    }

    @Test
    fun `test queenside castling black`() {
        val board = Board.fromFen("r3kbnr/ppp1pppp/2nq4/3p1b2/3P1B2/2NQ4/PPP1PPPP/R3KBNR b KQkq - 5 4")
        val move = SanMapper.fromSan(board, "O-O-O")
        assertEquals(Square(File.E, Rank.R8), move.from)
        assertEquals(Square(File.C, Rank.R8), move.to)
    }

    @Test
    fun `test promotion`() {
        val board = Board.fromFen("8/P7/8/8/8/8/8/k6K w - - 0 1")
        val move = SanMapper.fromSan(board, "a8=Q")
        assertEquals(Square(File.A, Rank.R7), move.from)
        assertEquals(Square(File.A, Rank.R8), move.to)
        assertEquals(PieceType.QUEEN, move.promotion)
    }

    @Test
    fun `test disambiguation file`() {
        val board = Board.fromFen("rnbqkbnr/ppp1pppp/8/3p4/8/5N1N/PPPPPPPP/RNBQKB1R w KQkq - 0 1")
        // Both knights can move to g5
        val move = SanMapper.fromSan(board, "Nhg5")
        assertEquals(Square(File.H, Rank.R3), move.from)
        assertEquals(Square(File.G, Rank.R5), move.to)
        
        val move2 = SanMapper.fromSan(board, "Nfg5")
        assertEquals(Square(File.F, Rank.R3), move2.from)
        assertEquals(Square(File.G, Rank.R5), move2.to)
    }

    @Test
    fun `test disambiguation rank`() {
        val board = Board.fromFen("8/8/8/8/3R4/8/3R4/K6k w - - 0 1")
        // Rooks on d2 and d4. Both can move to d3.
        val move = SanMapper.fromSan(board, "R2d3")
        assertEquals(Square(File.D, Rank.R2), move.from)
        assertEquals(Square(File.D, Rank.R3), move.to)
        
        val move2 = SanMapper.fromSan(board, "R4d3")
        assertEquals(Square(File.D, Rank.R4), move2.from)
        assertEquals(Square(File.D, Rank.R3), move2.to)
    }

    @Test
    fun `test disambiguation both`() {
        // Q at a1, a3, c1.
        val board = Board.fromFen("8/8/8/8/8/Q7/8/Q1Q4K w - - 0 1")
        val move = SanMapper.fromSan(board, "Qa1b2")
        assertEquals(Square(File.A, Rank.R1), move.from)
        assertEquals(Square(File.B, Rank.R2), move.to)
    }

    @Test
    fun `test check and mate symbols ignored`() {
        val board = Board.createDefault()
        val move = SanMapper.fromSan(board, "e4+")
        assertEquals(Square(File.E, Rank.R2), move.from)
        assertEquals(Square(File.E, Rank.R4), move.to)
        
        val moveMate = SanMapper.fromSan(board, "e4#")
        assertEquals(Square(File.E, Rank.R2), moveMate.from)
        assertEquals(Square(File.E, Rank.R4), moveMate.to)
    }

    @Test
    fun `test invalid SAN length`() {
        val board = Board.createDefault()
        assertThrows(InvalidSanNotationException::class.java) {
            SanMapper.fromSan(board, "e")
        }
    }

    @Test
    fun `test invalid target square`() {
        val board = Board.createDefault()
        assertThrows(InvalidSanNotationException::class.java) {
            SanMapper.fromSan(board, "e9")
        }
    }

    @Test
    fun `test ambiguous move throws exception`() {
        val board = Board.fromFen("rnbqkbnr/ppp1pppp/8/3p4/8/5N1N/PPPPPPPP/RNBQKB1R w KQkq - 0 1")
        assertThrows(AmbiguousMoveException::class.java) {
            SanMapper.fromSan(board, "Ng5")
        }
    }

    @Test
    fun `test no legal move throws exception`() {
        val board = Board.createDefault()
        assertThrows(IllegalMoveException::class.java) {
            SanMapper.fromSan(board, "e5")
        }
    }

    @Test
    fun `test capture without x throws exception`() {
        val board = Board.fromFen("rnbqkbnr/ppp1pppp/8/3p4/4P3/8/PPPP1PPP/RNBQKBNR w KQkq d6 0 2")
        assertThrows(IllegalMoveException::class.java) {
            SanMapper.fromSan(board, "ed5")
        }
    }
}
