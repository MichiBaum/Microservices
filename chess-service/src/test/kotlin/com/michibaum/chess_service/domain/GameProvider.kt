package com.michibaum.chess_service.domain

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