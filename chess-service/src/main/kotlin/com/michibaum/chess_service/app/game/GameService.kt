package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.apis.ApiService
import com.michibaum.chess_service.apis.dtos.GameDto
import com.michibaum.chess_service.database.*
import com.michibaum.chess_service.get
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

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
            val exists = gameRepository.existsByPlatformAndExternalGameId(game.platform, game.id)
            if(exists) continue

            val gameToSave = convertGame(game)
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

    private fun convertGame(gameDto: GameDto): Game {
        val whitePlayerDto = gameDto.players.find { it.pieceColor == PieceColor.WHITE }!!
        val blackPlayerDto = gameDto.players.find { it.pieceColor == PieceColor.BLACK }!!

        val whiteAccount = accountRepository.findByPlatformAndPlatformId(gameDto.platform, whitePlayerDto.id)
        val blackAccount = accountRepository.findByPlatformAndPlatformId(gameDto.platform, blackPlayerDto.id)

        val whitePlayer = Player(
            username = whitePlayerDto.username,
            rating = whitePlayerDto.rating,
            pieceColor = PieceColor.WHITE,
            account = whiteAccount
        )

        val blackPlayer = Player(
            username = blackPlayerDto.username,
            rating = blackPlayerDto.rating,
            pieceColor = PieceColor.BLACK,
            account = blackAccount
        )

        return Game(
            sourceType = SourceType.ONLINE_PLATFORM,
            platform = gameDto.platform,
            externalGameId = gameDto.id,
            pgn = gameDto.pgn,
            gameResult = gameDto.gameResult,
            playedAt = gameDto.playedAt,
            timeControlCategory = gameDto.timeControlCategory,
            whitePlayer = whitePlayer,
            blackPlayer = blackPlayer
        )
    }

    fun getByEvent(event: Event): List<Game> =
        gameRepository.findByEvent(event)

    fun findById(uuid: UUID) = gameRepository.findById(uuid).getOrNull()

}