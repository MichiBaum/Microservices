package com.michibaum.chess_service.app.game

import com.michibaum.chess_service.apis.ApiService
import com.michibaum.chess_service.apis.dtos.GameDto
import com.michibaum.chess_service.apis.dtos.PlayerDto
import com.michibaum.chess_service.database.*
import com.michibaum.chess_service.domain.AccountProvider
import com.michibaum.chess_service.domain.GameProvider
import com.michibaum.chess_service.domain.PlayerProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class GameServiceUT {

    private val gameRepository: GameRepository = mockk()
    private val apiService: ApiService = mockk()
    private val accountRepository: AccountRepository = mockk()
    private val gameService = GameService(gameRepository, apiService, accountRepository)

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
}