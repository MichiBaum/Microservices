package com.michibaum.chess_service.app.game.pgn

import com.michibaum.chess_service.domain.Game
import org.springframework.stereotype.Component

@Component
class PgnParser {

    private val splitter = PgnFileSplitter()
    private val parser = PgnMatchParser()
    private val formatter = PgnMatchFormatter()

    fun parsePgn(pgn: String): List<Match> {
        return splitter
            .split(pgn)
            .map { parser.parse(it) }
            .toList()
    }

    fun parseGame(game: Game){

    }

}