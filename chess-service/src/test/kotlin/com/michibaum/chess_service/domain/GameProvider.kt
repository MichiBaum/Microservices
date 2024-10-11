package com.michibaum.chess_service.domain

class GameProvider {

    companion object {
        fun game(): Game {
            return game(emptySet(), emptySet())
        }

        fun game(account: Account): Game {
            return game(setOf(account), emptySet())
        }

        fun game(player: Player): Game {
            return game(emptySet(), setOf(player))
        }

        fun game(accounts: Set<Account>, players: Set<Player>): Game {
            return Game(
                chessPlatform = ChessPlatform.CHESSCOM,
                platformId = "game1",
                pgn = "e4 e5",
                gameType = GameType.BLITZ,
                accounts = accounts,
                players = players
            )
        }
    }

}