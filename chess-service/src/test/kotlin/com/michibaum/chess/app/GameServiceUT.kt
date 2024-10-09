import com.michibaum.chess.apis.ApiService
import com.michibaum.chess.apis.dtos.GameDto
import com.michibaum.chess.apis.dtos.PlayerDto
import com.michibaum.chess.app.AccountRepository
import com.michibaum.chess.app.GameRepository
import com.michibaum.chess.app.GameService
import com.michibaum.chess.domain.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.verifyNoMoreInteractions
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
        val tmpGame = GameProvider.game(account)
        val player = PlayerProvider.player(tmpGame)
            .copy(username = account.username, platformId = account.accId)
        val game = tmpGame.copy(players = setOf(player))
        val gameDto =  GameDto(
            chessPlatform = game.chessPlatform,
            id = game.platformId,
            players = listOf(PlayerDto(
                id = player.platformId,
                username = player.username,
                rating = player.rating,
                pieceColor = player.pieceColor,
            )),
            pgn = game.pgn,
            gameType = game.gameType
        )

        every { apiService.getGames(account) } returns listOf(gameDto)
        every { gameRepository.existsByChessPlatformAndPlatformId(game.chessPlatform, game.platformId) } returns false
        every {
            accountRepository.findByPlatformAndAccIdAndUsername(
                account.platform,
                account.accId,
                account.username
            )
        } returns account
        every { gameRepository.save(any()) } returnsArgument 0

        // when
        gameService.loadGamesFor(account)

        // then
        verify(exactly = 1) { apiService.getGames(account) }

        verify(exactly = 1) {
            gameRepository.existsByChessPlatformAndPlatformId(game.chessPlatform, game.platformId)
        }

        verify(exactly = 1) {
            accountRepository.findByPlatformAndAccIdAndUsername(
                account.platform,
                account.accId,
                account.username
            )
        }

        verify(exactly = 1) { gameRepository.save(any()) }

        val gameSlot = slot<Game>()
        verify { gameRepository.save(capture(gameSlot)) }
        val savedGame = gameSlot.captured

        assertEquals(game.chessPlatform, savedGame.chessPlatform)
        assertEquals(game.platformId, savedGame.platformId)
        assertEquals(game.pgn, savedGame.pgn)
        assertEquals(game.gameType, savedGame.gameType)
        assertEquals(1, savedGame.players.size)

        val savedPlayer = savedGame.players.first()
        assertEquals(player.platformId, savedPlayer.platformId)
        assertEquals(player.username, savedPlayer.username)
        assertEquals(player.rating, savedPlayer.rating)
        assertEquals(player.pieceColor, savedPlayer.pieceColor)
    }

    @Test
    fun `should load games for account and skip existing games`() {
        // given
        val account = AccountProvider.account()
        val game = GameProvider.game(account)
        val gameDto =  GameDto(
            chessPlatform = game.chessPlatform,
            id = game.platformId,
            players = emptyList(),
            pgn = game.pgn,
            gameType = game.gameType
        )

        every { apiService.getGames(account) } returns listOf(gameDto)
        every { gameRepository.existsByChessPlatformAndPlatformId(game.chessPlatform, game.platformId) } returns true

        // when
        gameService.loadGamesFor(account)

        // then
        verify(exactly = 0) { gameRepository.save(game) }
        verify(exactly = 1) { apiService.getGames(account) }
        verify(exactly = 1) { gameRepository.existsByChessPlatformAndPlatformId(game.chessPlatform, game.platformId) }
    }

}