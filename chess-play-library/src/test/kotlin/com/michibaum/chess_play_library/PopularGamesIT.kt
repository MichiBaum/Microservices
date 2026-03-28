package com.michibaum.chess_play_library

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PopularGamesIT {
    
    @Test
    fun `Anatoly Karpov vs Garry Kasparov (1985 WC) - Game 16`(){
        var board = Board.createDefault()
        board = board.move("e4")
        board = board.move("c5")
        
        board = board.move("Nf3")
        board = board.move("e6")
        
        board = board.move("d4")
        board = board.move("cxd4")
        
        board = board.move("Nxd4")
        board = board.move("Nc6")
        
        board = board.move("Nb5")
        board = board.move("d6")
        
        board = board.move("c4")
        board = board.move("Nf6")
        
        board = board.move("N1c3")
        board = board.move("a6")
        
        board = board.move("Na3")
        board = board.move("d5")
        
        board = board.move("cxd5")
        board = board.move("exd5")
        
        board = board.move("exd5")
        board = board.move("Nb4")
        assertEquals("r1bqkb1r/1p3ppp/p4n2/3P4/1n6/N1N5/PP3PPP/R1BQKB1R w KQkq - 1 11", board.toFen().toString())
        
        board = board.move("Be2")
        board = board.move("Bc5")
        assertEquals("r1bqk2r/1p3ppp/p4n2/2bP4/1n6/N1N5/PP2BPPP/R1BQK2R w KQkq - 3 12", board.toFen().toString())
        
        board = board.move("O-O")
        board = board.move("O-O")
        assertEquals("r1bq1rk1/1p3ppp/p4n2/2bP4/1n6/N1N5/PP2BPPP/R1BQ1RK1 w - - 5 13", board.toFen().toString())
        
        board = board.move("Bf3")
        board = board.move("Bf5")
        
        board = board.move("Bg5")
        board = board.move("Re8")
        assertEquals("r2qr1k1/1p3ppp/p4n2/2bP1bB1/1n6/N1N2B2/PP3PPP/R2Q1RK1 w - - 9 15", board.toFen().toString())
        
        board = board.move("Qd2")
        board = board.move("b5")
        
        board = board.move("Rad1")
        board = board.move("Nd3")
        
        board = board.move("Nab1")
        board = board.move("h6")
        
        board = board.move("Bh4")
        board = board.move("b4")
        
        board = board.move("Na4")
        board = board.move("Bd6")
        
        board = board.move("Bg3")
        board = board.move("Rc8")
        assertEquals("2rqr1k1/5pp1/p2b1n1p/3P1b2/Np6/3n1BB1/PP1Q1PPP/1N1R1RK1 w - - 4 21", board.toFen().toString())
        
        board = board.move("b3")
        board = board.move("g5")
        
        board = board.move("Bxd6")
        board = board.move("Qxd6")
        
        board = board.move("g3")
        board = board.move("Nd7")
        
        board = board.move("Bg2")
        board = board.move("Qf6")
        
        board = board.move("a3")
        board = board.move("a5")
        
        board = board.move("axb4")
        board = board.move("axb4")
        
        board = board.move("Qa2")
        board = board.move("Bg6")
        
        board = board.move("d6")
        board = board.move("g4")
        
        board = board.move("Qd2")
        board = board.move("Kg7")
        
        board = board.move("f3")
        board = board.move("Qxd6")
        assertEquals("2r1r3/3n1pk1/3q2bp/8/Np4p1/1P1n1PP1/3Q2BP/1N1R1RK1 w - - 0 31", board.toFen().toString())
        
        board = board.move("fxg4")
        board = board.move("Qd4+")
        
        board = board.move("Kh1")
        board = board.move("Nf6")
        assertEquals("2r1r3/5pk1/5nbp/8/Np1q2P1/1P1n2P1/3Q2BP/1N1R1R1K w - - 3 33", board.toFen().toString())
        
        board = board.move("Rf4")
        board = board.move("Ne4")
        
        board = board.move("Qxd3")
        board = board.move("Nf2+")
        
        board = board.move("Rxf2")
        board = board.move("Bxd3")
        
        board = board.move("Rfd2")
        board = board.move("Qe3")
        
        board = board.move("Rxd3")
        board = board.move("Rc1")
        
        board = board.move("Nb2")
        board = board.move("Qf2")
        
        board = board.move("Nd2")
        board = board.move("Rxd1+")
        
        board = board.move("Nxd1")
        board = board.move("Re1+")
        assertEquals("8/5pk1/7p/8/1p4P1/1P1R2P1/3N1qBP/3Nr2K w - - 1 41", board.toFen().toString())
    }
}
