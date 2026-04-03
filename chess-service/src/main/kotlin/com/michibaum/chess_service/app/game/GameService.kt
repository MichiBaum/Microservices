package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.apis.ApiService
import com.michibaum.chess_service.apis.dtos.GameDto
import com.michibaum.chess_service.database.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class GameService(
    private val gameRepository: GameRepository,
    private val apiService: ApiService,
    private val accountRepository: AccountRepository,
    private val eventRepository: EventRepository
) {

    @Transactional
    fun create(gameRequestDto: GameRequestDto): Game {
        val event = gameRequestDto.eventId.let { eventRepository.findById(it).getOrNull() }
        val whitePlayerAccount =
            gameRequestDto.whitePlayer.accountId.let { accountRepository.findById(it).getOrNull() }
        val blackPlayerAccount =
            gameRequestDto.blackPlayer.accountId.let { accountRepository.findById(it).getOrNull() }

        val game = Game(
            sourceType = gameRequestDto.sourceType,
            platform = gameRequestDto.platform,
            externalGameId = gameRequestDto.externalGameId,
            pgn = gameRequestDto.pgn ?: "",
            openingName = gameRequestDto.openingName,
            gameResult = gameRequestDto.gameResult,
            termination = gameRequestDto.termination,
            playedAt = gameRequestDto.playedAt,
            timeControl = gameRequestDto.timeControl,
            timeControlCategory = gameRequestDto.timeControlCategory,
            variant = gameRequestDto.variant,
            whitePlayer = Player(
                username = gameRequestDto.whitePlayer.username,
                rating = gameRequestDto.whitePlayer.rating,
                pieceColor = PieceColor.WHITE,
                account = whitePlayerAccount
            ),
            blackPlayer = Player(
                username = gameRequestDto.blackPlayer.username,
                rating = gameRequestDto.blackPlayer.rating,
                pieceColor = PieceColor.BLACK,
                account = blackPlayerAccount
            ),
            event = event
        )
        return gameRepository.save(game)
    }

    @Transactional
    fun update(id: UUID, gameRequestDto: GameRequestDto): Game? {
        val game = findById(id) ?: return null

        val event = gameRequestDto.eventId.let { eventRepository.findById(it).getOrNull() }
        val whitePlayerAccount =
            gameRequestDto.whitePlayer.accountId.let { accountRepository.findById(it).getOrNull() }
        val blackPlayerAccount =
            gameRequestDto.blackPlayer.accountId.let { accountRepository.findById(it).getOrNull() }

        game.sourceType = gameRequestDto.sourceType
        game.platform = gameRequestDto.platform
        game.externalGameId = gameRequestDto.externalGameId
        game.pgn = gameRequestDto.pgn ?: ""
        game.openingName = gameRequestDto.openingName
        game.gameResult = gameRequestDto.gameResult
        game.termination = gameRequestDto.termination
        game.playedAt = gameRequestDto.playedAt
        game.timeControl = gameRequestDto.timeControl
        game.timeControlCategory = gameRequestDto.timeControlCategory
        game.variant = gameRequestDto.variant

        game.whitePlayer.username = gameRequestDto.whitePlayer.username
        game.whitePlayer.rating = gameRequestDto.whitePlayer.rating
        game.whitePlayer.account = whitePlayerAccount
        game.whitePlayer.pieceColor = PieceColor.WHITE

        game.blackPlayer.username = gameRequestDto.blackPlayer.username
        game.blackPlayer.rating = gameRequestDto.blackPlayer.rating
        game.blackPlayer.account = blackPlayerAccount
        game.blackPlayer.pieceColor = PieceColor.BLACK

        game.event = event

        return gameRepository.save(game)
    }

    @Transactional
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

    fun getByAccounts(id1: UUID, id2: UUID): List<Game> =
        gameRepository.findByAccounts(id1, id2)

    fun getByAccount(id: UUID): List<Game> =
        gameRepository.findByAccount(id)

    fun findById(uuid: UUID) = gameRepository.findById(uuid).getOrNull()
    
    @Transactional
    fun delete(uuid: UUID) {
        gameRepository.deleteById(uuid)
    }

}