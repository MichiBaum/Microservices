package com.michibaum.chess_service.database

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.domain.PlayerProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import java.time.LocalDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestcontainersConfiguration
class GameMappingIT {

    @Autowired
    lateinit var gameRepository: GameRepository

    @Test
    fun testSaveAndRetrieveGame() {
        val whitePlayer = PlayerProvider.player(pieceColor = PieceColor.WHITE, username = "WhitePlayer")
        val blackPlayer = PlayerProvider.player(pieceColor = PieceColor.BLACK, username = "BlackPlayer")

        val game = Game(
            sourceType = SourceType.ONLINE_PLATFORM,
            platform = ChessPlatform.LICHESS,
            externalGameId = "lichess123",
            pgn = "1. e4 e5 2. Nf3 Nc6",
            gameResult = GameResult.WHITE_WIN,
            termination = TerminationType.CHECKMATE,
            playedAt = LocalDateTime.now().minusDays(1),
            timeControl = "600+5",
            timeControlCategory = TimeControlCategory.RAPID,
            variant = GameVariant.STANDARD,
            whitePlayer = whitePlayer,
            blackPlayer = blackPlayer,
        )

        val savedGame = gameRepository.save(game)
        assertNotNull(savedGame.id)

        val retrievedGame = gameRepository.findById(savedGame.idOrThrow()).orElseThrow()
        assertEquals(SourceType.ONLINE_PLATFORM, retrievedGame.sourceType)
        assertEquals(ChessPlatform.LICHESS, retrievedGame.platform)
        assertEquals("lichess123", retrievedGame.externalGameId)
        assertEquals(GameResult.WHITE_WIN, retrievedGame.gameResult)
        assertEquals(TerminationType.CHECKMATE, retrievedGame.termination)
        assertEquals("WhitePlayer", retrievedGame.whitePlayer.username)
        assertEquals("BlackPlayer", retrievedGame.blackPlayer.username)
    }

    @Test
    fun testOTBGameMapping() {
        val whitePlayer = PlayerProvider.player(pieceColor = PieceColor.WHITE, username = "OTB White")
        val blackPlayer = PlayerProvider.player(pieceColor = PieceColor.BLACK, username = "OTB Black")

        val game = Game(
            sourceType = SourceType.OTB,
            pgn = "1. d4 d5",
            gameResult = GameResult.DRAW,
            playedAt = LocalDateTime.now(),
            whitePlayer = whitePlayer,
            blackPlayer = blackPlayer,
        )

        val savedGame = gameRepository.save(game)
        assertNotNull(savedGame.id)

        val retrievedGame = gameRepository.findById(savedGame.idOrThrow()).orElseThrow()
        assertEquals(SourceType.OTB, retrievedGame.sourceType)
        assertNull(retrievedGame.platform)
    }
}
