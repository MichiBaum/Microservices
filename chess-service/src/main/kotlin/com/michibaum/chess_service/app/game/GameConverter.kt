package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.database.Game
import org.springframework.stereotype.Component

@Component
class GameConverter {

    fun convert(game: Game): GameResponseDto {
        return GameResponseDto(
            id = game.idOrThrow(),
            sourceType = game.sourceType,
            platform = game.platform,
            externalGameId = game.externalGameId,
            pgn = game.pgn,
            openingName = game.openingName,
            gameResult = game.gameResult,
            termination = game.termination,
            playedAt = game.playedAt,
            importedAt = game.importedAt,
            timeControl = game.timeControl,
            timeControlCategory = game.timeControlCategory,
            variant = game.variant,
            whitePlayer = convertPlayer(game.whitePlayer),
            blackPlayer = convertPlayer(game.blackPlayer),
            fen = game.fen,
            round = game.round,
            boardNumber = game.boardNumber
        )
    }

    private fun convertPlayer(player: com.michibaum.chess_service.database.Player): PlayerResponseDto {
        return PlayerResponseDto(
            id = player.idOrThrow(),
            username = player.username,
            rating = player.rating,
            pieceColor = player.pieceColor,
            accountId = player.account?.id
        )
    }

}