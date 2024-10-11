package com.michibaum.chess_service.app

import com.michibaum.chess_service.apis.ApiService
import com.michibaum.chess_service.apis.dtos.GameDto
import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.Game
import com.michibaum.chess_service.domain.Player
import org.springframework.stereotype.Service
import java.util.*

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val apiService: ApiService,
    private val accountRepository: AccountRepository
) {

    fun loadGamesFor(account: Account){
        val games = apiService.getGames(account)

        for (game in games){
            val exists = gameRepository.existsByChessPlatformAndPlatformId(game.chessPlatform, game.id)
            if(exists) continue

            val accounts = game.players.mapNotNull {
                accountRepository.findByPlatformAndAccIdAndUsername(
                    game.chessPlatform,
                    it.id,
                    it.username
                )
            }.toSet()

            val gameToSave = convertGame(game, accounts)
            gameRepository.save(gameToSave)
        }

    }

    private fun convertGame(gameDto: GameDto, accounts: Set<Account>): Game {
        val game = Game(
            chessPlatform = gameDto.chessPlatform,
            platformId = gameDto.id,
            pgn = gameDto.pgn,
            gameType = gameDto.gameType,
            accounts = accounts,
            players = mutableSetOf(),
            id = UUID.randomUUID()
        )

        val players = gameDto.players.map {
            Player(
                platformId = it.id,
                username = it.username,
                rating = it.rating,
                pieceColor = it.pieceColor,
                game = game,
                id = UUID.randomUUID()
            )
        }.toSet()

        (game.players as MutableSet<Player>).addAll(players) // TODO fuck immutable

        return game

    }

}