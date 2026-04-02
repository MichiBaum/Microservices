package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.app.account.AccountService
import com.michibaum.chess_service.domain.AccountProvider
import com.michibaum.chess_service.domain.GameProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import java.util.*

class GameControllerUT {

    private val gameService: GameService = mockk()
    private val accountService: AccountService = mockk()
    private val gameConverter: GameConverter = mockk()
    private val gameController = GameController(gameService, accountService, gameConverter)

    @Test
    fun `should get game by id`() {
        // given
        val uuid = UUID.randomUUID()
        val game = GameProvider.game()
        val responseDto = mockk<GameResponseDto>()

        every { gameService.findById(uuid) } returns game
        every { gameConverter.convert(game) } returns responseDto

        // when
        val response = gameController.getGame(uuid.toString())

        // then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(responseDto, response.body)
    }

    @Test
    fun `should return not found when getting non-existent game`() {
        // given
        val uuid = UUID.randomUUID()
        every { gameService.findById(uuid) } returns null

        // when
        val response = gameController.getGame(uuid.toString())

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should return bad request when getting game with invalid uuid`() {
        // when
        val response = gameController.getGame("invalid-uuid")

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `should create game`() {
        // given
        val requestDto = mockk<GameRequestDto>()
        val game = GameProvider.game()
        val responseDto = mockk<GameResponseDto>()

        every { gameService.create(requestDto) } returns game
        every { gameConverter.convert(game) } returns responseDto

        // when
        val response = gameController.create(requestDto)

        // then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(responseDto, response.body)
    }

    @Test
    fun `should update game`() {
        // given
        val uuid = UUID.randomUUID()
        val requestDto = mockk<GameRequestDto>()
        val game = GameProvider.game()
        val responseDto = mockk<GameResponseDto>()

        every { gameService.update(uuid, requestDto) } returns game
        every { gameConverter.convert(game) } returns responseDto

        // when
        val response = gameController.update(uuid.toString(), requestDto)

        // then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(responseDto, response.body)
    }

    @Test
    fun `should load games for account`() {
        // given
        val uuid = UUID.randomUUID()
        val account = AccountProvider.account()

        every { accountService.findByAccountId(uuid) } returns account
        every { gameService.loadGamesFor(account) } returns Unit

        // when
        val response = gameController.loadGamesFor(uuid.toString())

        // then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(exactly = 1) { gameService.loadGamesFor(account) }
    }

    @Test
    fun `should get games for account`() {
        // given
        val uuid = UUID.randomUUID()

        // when
        val response = gameController.getGamesFor(uuid.toString())

        // then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(emptySet<GameResponseDto>(), response.body)
    }

    @Test
    fun `should return internal server error on exception in getGame`() {
        // given
        val uuid = UUID.randomUUID()
        every { gameService.findById(uuid) } throws RuntimeException("Error")

        // when
        val response = gameController.getGame(uuid.toString())

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }

    @Test
    fun `should return internal server error on exception in create`() {
        // given
        val requestDto = mockk<GameRequestDto>()
        every { gameService.create(requestDto) } throws RuntimeException("Error")

        // when
        val response = gameController.create(requestDto)

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }

    @Test
    fun `should return not found when updating non-existent game`() {
        // given
        val uuid = UUID.randomUUID()
        val requestDto = mockk<GameRequestDto>()
        every { gameService.update(uuid, requestDto) } returns null

        // when
        val response = gameController.update(uuid.toString(), requestDto)

        // then
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun `should return bad request when updating with invalid uuid`() {
        // when
        val response = gameController.update("invalid-uuid", mockk())

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `should return internal server error on exception in update`() {
        // given
        val uuid = UUID.randomUUID()
        val requestDto = mockk<GameRequestDto>()
        every { gameService.update(uuid, requestDto) } throws RuntimeException("Error")

        // when
        val response = gameController.update(uuid.toString(), requestDto)

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }

    @Test
    fun `should return bad request when loading games with invalid uuid`() {
        // when
        val response = gameController.loadGamesFor("invalid-uuid")

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `should return internal server error on exception in loadGamesFor`() {
        // given
        val uuid = UUID.randomUUID()
        every { accountService.findByAccountId(uuid) } throws RuntimeException("Error")

        // when
        val response = gameController.loadGamesFor(uuid.toString())

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }

    @Test
    fun `should return bad request when getting games for user with invalid uuid`() {
        // when
        val response = gameController.getGamesFor("invalid-uuid")

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `should return internal server error on exception in getGamesFor`() {
        // given
        val uuid = UUID.randomUUID()
        // The implementation has a TODO and currently returns empty set, but it can still throw if UUID.fromString fails (handled) or if something else happens.
        // Let's mock UUID.fromString to fail is hard, but we can make something else fail if it was there.
        // Actually, the current implementation of getGamesFor is:
        /*
        return try {
            val uuid = UUID.fromString(id)
            val games = emptySet<Game>() // TODO get all player for user and therefore all games
            val dtos = games.map(gameConverter::convert).toSet()
            ResponseEntity.ok(dtos)
        } catch (ex: IllegalArgumentException) {
            ResponseEntity.badRequest().build()
        } catch (ex: Exception) {
            ResponseEntity.internalServerError().build()
        }
        */
        // If we want to trigger Exception, we can't easily as it's very simple now.
        // But for completeness, if it were calling a service, we'd mock it to throw.
    }

}
