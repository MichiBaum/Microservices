package com.michibaum.chess.apis.chesscom

import com.michibaum.chess.apis.dtos.*
import com.michibaum.chess.apis.dtos.Player
import com.michibaum.chess.domain.ChessPlatform
import com.michibaum.chess.domain.GameType
import org.springframework.stereotype.Component
import java.util.*

@Component(value = "chesscomConverter")
class Converter {

    fun convert(accountDto: ChesscomAccountDto): AccountDto =
        AccountDto(
            id = accountDto.playerId.toString(),
            url = accountDto.url,
            username = accountDto.username,
            name = accountDto.name,
            platform = ChessPlatform.CHESSCOM,
            createdAt = Date(accountDto.joined * 1000)
        )

    fun convert(gameDto: ChesscomGameDto): GameDto {
        val player1 = gameDto.white?.let {
            Player(id = it.uuid, username = it.username, rating = it.rating, pieceColor = PieceColor.WHITE)
        }

        val player2 = gameDto.black?.let {
            Player(id = it.uuid, username = it.username, rating = it.rating, pieceColor = PieceColor.BLACK)
        }

        val gametype = when(gameDto.timeClass){
            "bullet" -> GameType.BULLET
            "blitz" -> GameType.BLITZ
            "rapid" -> GameType.RAPID
            else -> GameType.UNKNOWN
        }

        return GameDto(
            chessPlatform = ChessPlatform.CHESSCOM,
            id = gameDto.uuid ?: "",
            players = listOfNotNull(player1, player2),
            pgn = gameDto.pgn ?: "",
            gameType = gametype
        )
    }

    fun convert(stats: ChesscomStatsDto): StatsDto =
        StatsDto(
            bullet = Rating(
                current = stats.chessBullet?.last?.rating,
                lowest = null,
                highest = stats.chessBullet?.best?.rating
            ),
            blitz = Rating(
                current = stats.chessBlitz?.last?.rating,
                lowest = null,
                highest = stats.chessBlitz?.best?.rating
            ),
            rapid = Rating(
                current = stats.chessRapid?.last?.rating,
                lowest = null,
                highest = stats.chessRapid?.best?.rating
            ),
        )

}