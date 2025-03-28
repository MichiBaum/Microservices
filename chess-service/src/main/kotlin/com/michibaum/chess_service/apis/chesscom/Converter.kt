package com.michibaum.chess_service.apis.chesscom

import com.michibaum.chess_service.apis.dtos.*
import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.GameType
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Component(value = "chesscomConverter")
class Converter {

    fun convert(accountDto: ChesscomAccountDto): AccountDto =
        AccountDto(
            id = accountDto.playerId.toString(),
            url = accountDto.url,
            username = accountDto.username,
            name = accountDto.name,
            platform = ChessPlatform.CHESSCOM,
            createdAt = LocalDate.ofInstant(Instant.ofEpochSecond(accountDto.joined), ZoneId.systemDefault()) // TODO dont know zone
        )

    fun convert(gameDto: ChesscomGameDto): GameDto {
        val player1 = gameDto.white?.let {
            PlayerDto(id = it.uuid, username = it.username, rating = it.rating, pieceColor = PieceColor.WHITE)
        }

        val player2 = gameDto.black?.let {
            PlayerDto(id = it.uuid, username = it.username, rating = it.rating, pieceColor = PieceColor.BLACK)
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

    fun convert(leaderboards: ChesscomLeaderboards): List<TopAccountDto> {
        val topBullet = leaderboards.liveBullet.map { TopAccountDto(it.username.lowercase()) } // TODO BUG example: usernames are MagnusCarlsen. Player endpoint accepts magnuscarlsen
        val topBlitz = leaderboards.liveBlitz.map { TopAccountDto(it.username.lowercase()) } // TODO BUG example: usernames are MagnusCarlsen. Player endpoint accepts magnuscarlsen
        val topRapid = leaderboards.liveRapid.map { TopAccountDto(it.username.lowercase()) } // TODO BUG example: usernames are MagnusCarlsen. Player endpoint accepts magnuscarlsen

        return topBullet + topBlitz + topRapid

    }

}