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
        val game = GameProvider.game()
        val responseDto = mockk<GameResponseDto>()

        every { gameService.getByAccount(uuid) } returns listOf(game)
        every { gameConverter.convert(game) } returns responseDto

        // when
        val response = gameController.getGamesFor(uuid.toString())

        // then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf(responseDto), response.body)
    }

    @Test
    fun `should get games between two accounts`() {
        // given
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val ids = listOf(uuid1.toString(), uuid2.toString())
        val game = GameProvider.game()
        val responseDto = mockk<GameResponseDto>()

        every { gameService.getByAccounts(uuid1, uuid2) } returns listOf(game)
        every { gameConverter.convert(game) } returns responseDto

        // when
        val response = gameController.getGamesBetween(ids)

        // then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(listOf(responseDto), response.body)
    }

    @Test
    fun `should return bad request when getting games between with invalid number of accounts`() {
        // when
        val response = gameController.getGamesBetween(listOf("id1"))

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
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
    fun `should delete game`() {
        // given
        val uuid = UUID.randomUUID()
        every { gameService.delete(uuid) } returns Unit

        // when
        val response = gameController.delete(uuid.toString())

        // then
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(exactly = 1) { gameService.delete(uuid) }
    }

    @Test
    fun `should return bad request when deleting with invalid uuid`() {
        // when
        val response = gameController.delete("invalid-uuid")

        // then
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun `should return internal server error on exception in delete`() {
        // given
        val uuid = UUID.randomUUID()
        every { gameService.delete(uuid) } throws RuntimeException("Error")

        // when
        val response = gameController.delete(uuid.toString())

        // then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
    }

}
