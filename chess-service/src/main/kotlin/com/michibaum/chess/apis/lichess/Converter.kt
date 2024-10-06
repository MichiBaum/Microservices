package com.michibaum.chess.apis.lichess

import com.michibaum.chess.apis.dtos.*
import com.michibaum.chess.apis.dtos.Player
import com.michibaum.chess.domain.ChessPlatform
import com.michibaum.chess.domain.GameType
import org.springframework.stereotype.Component
import java.util.*

@Component(value = "lichessConverter")
class Converter {

    fun convert(accountDto: LichessAccountDto): AccountDto =
        AccountDto(
            id = accountDto.id,
            url = accountDto.url,
            username = accountDto.username,
            name = accountDto.profile.realName,
            platform = ChessPlatform.LICHESS,
            createdAt = Date(accountDto.createdAt)
        )

    fun convert(gameDto: LichessGameDto): GameDto {
        val player1 = gameDto.players.white.let {
            Player(
                id = it.user?.id ?: "",
                username = it.user?.name ?: "",
                rating = it.rating ?: 0,
                pieceColor = PieceColor.WHITE
            )
        }

        val player2 = gameDto.players.black.let {
            Player(
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
            pgn = gameDto.pgn,
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

}