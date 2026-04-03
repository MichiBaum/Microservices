package com.michibaum.chess_service.domain

import com.michibaum.chess_service.database.*
import java.time.LocalDateTime

class GameProvider {

    companion object {
        fun game(
            whitePlayer: Player = PlayerProvider.player(pieceColor = PieceColor.WHITE),
            blackPlayer: Player = PlayerProvider.player(pieceColor = PieceColor.BLACK),
            event: Event? = null
        ): Game {
            return Game(
                sourceType = SourceType.ONLINE_PLATFORM,
                platform = ChessPlatform.CHESSCOM,
                externalGameId = "game1",
                pgn = "e4 e5",
                gameResult = GameResult.ONGOING,
                playedAt = LocalDateTime.now(),
                whitePlayer = whitePlayer,
                blackPlayer = blackPlayer,
                event = event
            )
        }
    }

}