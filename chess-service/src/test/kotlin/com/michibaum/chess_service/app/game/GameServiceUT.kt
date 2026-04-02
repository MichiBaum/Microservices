package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.apis.ApiService
import com.michibaum.chess_service.apis.dtos.GameDto
import com.michibaum.chess_service.apis.dtos.PlayerDto
import com.michibaum.chess_service.database.*
import com.michibaum.chess_service.domain.AccountProvider
import com.michibaum.chess_service.domain.EventProvider
import com.michibaum.chess_service.domain.GameProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class GameServiceUT {

    private val gameRepository: GameRepository = mockk()
    private val apiService: ApiService = mockk()
    private val accountRepository: AccountRepository = mockk()
    private val eventRepository: EventRepository = mockk()
    private val gameService = GameService(gameRepository, apiService, accountRepository, eventRepository)

    @Test
    fun `should load games for account and save new games`() {
        // given
        val account = AccountProvider.account()
        val game = GameProvider.game()
        val whitePlayer = game.whitePlayer
        val blackPlayer = game.blackPlayer

        val gameDto = GameDto(
            platform = game.platform!!,
            id = game.externalGameId!!,
            players = listOf(
                PlayerDto(
                    id = account.platformId,
                    username = whitePlayer.username!!,
                    rating = whitePlayer.rating,
                    pieceColor = PieceColor.WHITE
                ),
                PlayerDto(
                    id = "otherId",
                    username = blackPlayer.username!!,
                    rating = blackPlayer.rating,
                    pieceColor = PieceColor.BLACK
                )
            ),
            pgn = game.pgn,
            timeControlCategory = TimeControlCategory.BLITZ,
            gameResult = GameResult.ONGOING,
            playedAt = game.playedAt
        )

        every { apiService.getGames(account) } returns listOf(gameDto)
        every { gameRepository.existsByPlatformAndExternalGameId(gameDto.platform, gameDto.id) } returns false
        every { accountRepository.findByPlatformAndPlatformId(gameDto.platform, any()) } returns null
        every { accountRepository.findByPlatformAndPlatformId(gameDto.platform, account.platformId) } returns account
        every { gameRepository.saveAll(any<List<Game>>()) } returns emptyList()

        // when
        gameService.loadGamesFor(account)

        // then
        verify(exactly = 1) { apiService.getGames(account) }
        verify(exactly = 1) { gameRepository.existsByPlatformAndExternalGameId(gameDto.platform, gameDto.id) }
        verify(exactly = 1) { gameRepository.saveAll(any<List<Game>>()) }
    }

    @Test
    fun `should load games for account and skip existing games`() {
        // given
        val account = AccountProvider.account()
        val game = GameProvider.game()
        val gameDto = GameDto(
            platform = game.platform!!,
            id = game.externalGameId!!,
            players = emptyList(),
            pgn = game.pgn,
            timeControlCategory = TimeControlCategory.BLITZ,
            gameResult = GameResult.ONGOING,
            playedAt = game.playedAt
        )

        every { apiService.getGames(account) } returns listOf(gameDto)
        every { gameRepository.existsByPlatformAndExternalGameId(gameDto.platform, gameDto.id) } returns true

        // when
        gameService.loadGamesFor(account)

        // then
        verify(exactly = 0) { gameRepository.saveAll(any<List<Game>>()) }
        verify(exactly = 1) { apiService.getGames(account) }
        verify(exactly = 1) { gameRepository.existsByPlatformAndExternalGameId(gameDto.platform, gameDto.id) }
    }

    @Test
    fun `should load games for account and save in batches`() {
        // given
        val account = AccountProvider.account()
        val games = (1..105).map { i ->
            GameDto(
                platform = ChessPlatform.CHESSCOM,
                id = "game-$i",
                players = listOf(
                    PlayerDto(
                        id = "white-id-$i",
                        username = "white-$i",
                        rating = 1500,
                        pieceColor = PieceColor.WHITE
                    ),
                    PlayerDto(
                        id = "black-id-$i",
                        username = "black-$i",
                        rating = 1500,
                        pieceColor = PieceColor.BLACK
                    )
                ),
                pgn = "pgn-$i",
                timeControlCategory = TimeControlCategory.BLITZ,
                gameResult = GameResult.ONGOING,
                playedAt = LocalDateTime.now()
            )
        }

        every { apiService.getGames(account) } returns games
        every { gameRepository.existsByPlatformAndExternalGameId(any(), any()) } returns false
        every { accountRepository.findByPlatformAndPlatformId(any(), any()) } returns null
        every { gameRepository.saveAll(any<List<Game>>()) } returns emptyList()

        // when
        gameService.loadGamesFor(account)

        // then
        verify(exactly = 2) { gameRepository.saveAll(any<List<Game>>()) }
        verify(exactly = 105) { gameRepository.existsByPlatformAndExternalGameId(any(), any()) }
    }

    @Test
    fun `should create a game`() {
        // given
        val eventId = UUID.randomUUID()
        val whiteAccountId = UUID.randomUUID()
        val blackAccountId = UUID.randomUUID()
        val playedAt = LocalDateTime.now()

        val gameRequestDto = GameRequestDto(
            sourceType = SourceType.ONLINE_PLATFORM,
            platform = ChessPlatform.CHESSCOM,
            externalGameId = "ext-1",
            pgn = "e4 e5",
            openingName = "Open",
            gameResult = GameResult.ONGOING,
            termination = TerminationType.CHECKMATE,
            playedAt = playedAt,
            timeControl = "600",
            timeControlCategory = TimeControlCategory.RAPID,
            variant = GameVariant.STANDARD,
            whitePlayer = PlayerRequestDto(whiteAccountId, "white", 1500),
            blackPlayer = PlayerRequestDto(blackAccountId, "black", 1500),
            eventId = eventId
        )

        val event = EventProvider.event()
        val whiteAccount = AccountProvider.account("white")
        val blackAccount = AccountProvider.account("black")

        every { eventRepository.findById(eventId) } returns Optional.of(event)
        every { accountRepository.findById(whiteAccountId) } returns Optional.of(whiteAccount)
        every { accountRepository.findById(blackAccountId) } returns Optional.of(blackAccount)
        every { gameRepository.save(any()) } answers { firstArg() }

        // when
        val createdGame = gameService.create(gameRequestDto)

        // then
        assertEquals(SourceType.ONLINE_PLATFORM, createdGame.sourceType)
        assertEquals(ChessPlatform.CHESSCOM, createdGame.platform)
        assertEquals("ext-1", createdGame.externalGameId)
        assertEquals("e4 e5", createdGame.pgn)
        assertEquals("Open", createdGame.openingName)
        assertEquals(GameResult.ONGOING, createdGame.gameResult)
        assertEquals(TerminationType.CHECKMATE, createdGame.termination)
        assertEquals(playedAt, createdGame.playedAt)
        assertEquals("600", createdGame.timeControl)
        assertEquals(TimeControlCategory.RAPID, createdGame.timeControlCategory)
        assertEquals(GameVariant.STANDARD, createdGame.variant)
        assertEquals("white", createdGame.whitePlayer.username)
        assertEquals(whiteAccount, createdGame.whitePlayer.account)
        assertEquals("black", createdGame.blackPlayer.username)
        assertEquals(blackAccount, createdGame.blackPlayer.account)
        assertEquals(event, createdGame.event)

        verify(exactly = 1) { gameRepository.save(any()) }
    }

    @Test
    fun `should update a game`() {
        // given
        val gameId = UUID.randomUUID()
        val existingGame = GameProvider.game()
        val eventId = UUID.randomUUID()
        val whiteAccountId = UUID.randomUUID()
        val blackAccountId = UUID.randomUUID()
        val playedAt = LocalDateTime.now()

        val gameRequestDto = GameRequestDto(
            sourceType = SourceType.OTB,
            platform = ChessPlatform.CHESSCOM,
            externalGameId = "updated-ext",
            pgn = "d4 d5",
            openingName = "Queen's Gambit",
            gameResult = GameResult.WHITE_WIN,
            termination = TerminationType.CHECKMATE,
            playedAt = playedAt,
            timeControl = "900",
            timeControlCategory = TimeControlCategory.RAPID,
            variant = GameVariant.STANDARD,
            whitePlayer = PlayerRequestDto(whiteAccountId, "updated-white", 1600),
            blackPlayer = PlayerRequestDto(blackAccountId, "updated-black", 1400),
            eventId = eventId
        )

        val event = EventProvider.event()
        val whiteAccount = AccountProvider.account("updated-white")
        val blackAccount = AccountProvider.account("updated-black")

        every { gameRepository.findById(gameId) } returns Optional.of(existingGame)
        every { eventRepository.findById(eventId) } returns Optional.of(event)
        every { accountRepository.findById(whiteAccountId) } returns Optional.of(whiteAccount)
        every { accountRepository.findById(blackAccountId) } returns Optional.of(blackAccount)
        every { gameRepository.save(any()) } answers { firstArg() }

        // when
        val updatedGame = gameService.update(gameId, gameRequestDto)

        // then
        assertNotNull(updatedGame)
        assertEquals(SourceType.OTB, updatedGame!!.sourceType)
        assertEquals("Queen's Gambit", updatedGame.openingName)
        assertEquals(GameResult.WHITE_WIN, updatedGame.gameResult)
        assertEquals("updated-white", updatedGame.whitePlayer.username)
        assertEquals(whiteAccount, updatedGame.whitePlayer.account)
        assertEquals(event, updatedGame.event)

        verify(exactly = 1) { gameRepository.save(any()) }
    }

    @Test
    fun `should return null when updating non-existent game`() {
        // given
        val gameId = UUID.randomUUID()
        val gameRequestDto = mockk<GameRequestDto>()

        every { gameRepository.findById(gameId) } returns Optional.empty()

        // when
        val updatedGame = gameService.update(gameId, gameRequestDto)

        // then
        assertNull(updatedGame)
        verify(exactly = 0) { gameRepository.save(any()) }
    }

    @Test
    fun `should find by id`() {
        // given
        val gameId = UUID.randomUUID()
        val game = GameProvider.game()
        every { gameRepository.findById(gameId) } returns Optional.of(game)

        // when
        val result = gameService.findById(gameId)

        // then
        assertEquals(game, result)
    }

    @Test
    fun `should get games by event`() {
        // given
        val event = EventProvider.event()
        val games = listOf(GameProvider.game())
        every { gameRepository.findByEvent(event) } returns games

        // when
        val result = gameService.getByEvent(event)

        // then
        assertEquals(games, result)
    }
}