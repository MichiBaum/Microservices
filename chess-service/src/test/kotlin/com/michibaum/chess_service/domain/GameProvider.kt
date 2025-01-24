package com.michibaum.chess_service.domain

import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.Game
import com.michibaum.chess_service.database.GameType
import com.michibaum.chess_service.database.Player

class GameProvider {

    companion object {
        fun game(): Game {
            return game(emptySet())
        }

        fun game(player: Player): Game {
            return game(setOf(player))
        }

        fun game(players: Set<Player>): Game {
            return Game(
                chessPlatform = ChessPlatform.CHESSCOM,
                platformId = "game1",
                pgn = "e4 e5",
                gameType = GameType.BLITZ,
                players = players
            )
        }
    }

}