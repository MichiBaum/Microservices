package com.michibaum.chess_service.apis.lichess

import com.michibaum.chess_service.apis.dtos.*
import com.michibaum.chess_service.database.*
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
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
                rating = it.rating,
                pieceColor = PieceColor.WHITE
            )
        }

        val player2 = gameDto.players.black.let {
            PlayerDto(
                id = it.user?.id ?: "",
                username = it.user?.name ?: "",
                rating = it.rating,
                pieceColor = PieceColor.BLACK
            )
        }

        val timeControlCategory = when(gameDto.perf){
            "bullet" -> TimeControlCategory.BULLET
            "blitz" -> TimeControlCategory.BLITZ
            "rapid" -> TimeControlCategory.RAPID
            "classical" -> TimeControlCategory.CLASSICAL
            "correspondence" -> TimeControlCategory.CORRESPONDENCE
            else -> TimeControlCategory.RAPID // Default
        }

        val result = when(gameDto.winner){
            "white" -> GameResult.WHITE_WIN
            "black" -> GameResult.BLACK_WIN
            else -> GameResult.DRAW
        }

        return GameDto(
            platform = ChessPlatform.LICHESS,
            id = gameDto.id,
            players = listOfNotNull(player1, player2),
            pgn = gameDto.pgn ?: "",
            timeControlCategory = timeControlCategory,
            gameResult = result,
            playedAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(gameDto.createdAt), ZoneId.systemDefault())
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