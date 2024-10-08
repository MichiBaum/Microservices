package com.michibaum.chess.app

import com.michibaum.chess.apis.ApiService
import com.michibaum.chess.domain.Account
import com.michibaum.chess.domain.Game
import com.michibaum.chess.domain.Player
import org.springframework.stereotype.Service

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

            val newGame = Game(
                chessPlatform = game.chessPlatform,
                platformId = game.id,
                pgn = game.pgn,
                gameType = game.gameType,
                accounts = accounts,
                players = emptySet()
            )

            val players = game.players.map { Player(
                platformId = it.id,
                username = it.username,
                rating = it.rating,
                pieceColor = it.pieceColor,
                game = newGame
            ) }.toSet()

            newGame.copy(players = players)
            gameRepository.save(newGame)
        }

    }

}