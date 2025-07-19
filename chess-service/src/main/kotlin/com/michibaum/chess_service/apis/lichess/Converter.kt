package com.michibaum.chess_service.apis.lichess

import com.michibaum.chess_service.apis.dtos.*
import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.GameType
import com.michibaum.chess_service.database.PieceColor
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Component(value = "lichessConverter")
class Converter {

    fun convert(accountDto: LichessAccountDto): AccountDto =
        AccountDto(
            id = accountDto.id,
            url = accountDto.url ?: "",
            username = accountDto.username,
            name = accountDto.profile?.realName ?: "",
            platform = ChessPlatform.LICHESS,
            createdAt = LocalDate.ofInstant(Instant.ofEpochMilli(accountDto.createdAt), ZoneId.systemDefault())
        )

    fun convert(gameDto: LichessGameDto): GameDto {
        val player1 = gameDto.players.white.let {
            PlayerDto(
                id = it.user?.id ?: "",
                username = it.user?.name ?: "",
                rating = it.rating ?: 0,
                pieceColor = PieceColor.WHITE
            )
        }

        val player2 = gameDto.players.black.let {
            PlayerDto(
                id = it.user?.id ?: "",
                username = it.user?.name ?: "",
                rating = it.rating ?: 0,
                pieceColor = PieceColor.BLACK
            )
        }

        val gametype = when(gameDto.perf){
            "bullet" -> GameType.BULLET
            "blitz" -> GameType.BLITZ
            "rapid" -> GameType.RAPID
            else -> GameType.UNKNOWN
        }

        return GameDto(
            chessPlatform = ChessPlatform.LICHESS,
            id = gameDto.id,
            players = listOfNotNull(player1, player2),
            pgn = gameDto.pgn ?: "",
            gameType = gametype
        )
    }

    fun convert(bullet: LichessStatsDto, blitz: LichessStatsDto, rapid: LichessStatsDto): StatsDto =
        StatsDto(
            bullet = Rating(
                current = bullet.perf.glicko.rating,
                lowest = bullet.stat.lowest?.int,
                highest = bullet.stat.highest?.int
            ),
            blitz = Rating(
                current = blitz.perf.glicko.rating,
                lowest = blitz.stat.lowest?.int,
                highest = blitz.stat.highest?.int
            ),
            rapid = Rating(
                current = rapid.perf.glicko.rating,
                lowest = rapid.stat.lowest?.int,
                highest = rapid.stat.highest?.int
            )
        )

    fun convert(leaderboards: LichessLeaderboards): List<TopAccountDto> {
        val topUltraBullet = leaderboards.ultraBullet.map { TopAccountDto(it.username) }
        val topBullet = leaderboards.bullet.map { TopAccountDto(it.username) }
        val topBlitz = leaderboards.blitz.map { TopAccountDto(it.username) }
        val topRapid = leaderboards.rapid.map { TopAccountDto(it.username) }
        val topClassical = leaderboards.classical.map { TopAccountDto(it.username) }

        return topUltraBullet + topBullet + topBlitz + topRapid + topClassical

    }

}