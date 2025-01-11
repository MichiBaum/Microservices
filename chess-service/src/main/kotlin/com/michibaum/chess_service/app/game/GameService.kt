package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.apis.ApiService
import com.michibaum.chess_service.apis.dtos.GameDto
import com.michibaum.chess_service.app.account.AccountRepository
import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.Event
import com.michibaum.chess_service.domain.Game
import com.michibaum.chess_service.domain.Player
import com.michibaum.chess_service.get
import org.springframework.stereotype.Service

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val apiService: ApiService,
    private val accountRepository: AccountRepository
) {

    fun loadGamesFor(account: Account){
        val games = apiService.getGames(account)

        val gamesToSave = mutableListOf<Game>()
        for (game in games){
            val exists = gameRepository.existsByChessPlatformAndPlatformId(game.chessPlatform, game.id)
            if(exists) continue

            val accounts = game.players.mapNotNull { // TODO doesnt work for chess.com playerId = UUID and accountId something like 3889224. Can not do with username because lower and upercase change somtimes
                accountRepository.findByPlatformAndPlatformIdAndUsername(
                    game.chessPlatform,
                    it.id,
                    it.username
                )
            }.toSet()

            val gameToSave = convertGame(game, accounts)
            gamesToSave.add(gameToSave)
            if (gamesToSave.size == 100) {
                gameRepository.saveAll(gamesToSave)
                gamesToSave.clear()
            }
        }
        if (gamesToSave.isNotEmpty()) {
            gameRepository.saveAll(gamesToSave)
            gamesToSave.clear()
        }
    }

    private fun convertGame(gameDto: GameDto, accounts: Set<Account>): Game {
        val game = Game(
            chessPlatform = gameDto.chessPlatform,
            platformId = gameDto.id,
            pgn = gameDto.pgn,
            gameType = gameDto.gameType,
            players = mutableSetOf()
        )


        val players = gameDto.players.map { // TODO mapping
            Player(
                username = it.username,
                rating = it.rating,
                pieceColor = it.pieceColor,
                account = accounts[0],
                game = game
            )
        }.toSet()

        (game.players as MutableSet<Player>).addAll(players) // TODO fuck immutable

        return game

    }

    fun getByEvent(event: Event): List<Game> =
        gameRepository.findByEvent(event)

}