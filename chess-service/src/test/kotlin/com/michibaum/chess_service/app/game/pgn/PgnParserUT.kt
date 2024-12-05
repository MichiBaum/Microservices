package com.michibaum.chess_service.app.game.pgn

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import java.io.InputStream
import java.nio.charset.StandardCharsets


class PgnParserUT {

    @Test
    fun `test 1`() {
        // GIVEN
        val pgn = getPgn("Fischer_Spassky_FS_Return_Match.pgn")

        val match = Match(
            event = "F/S Return Match",
            site = "Belgrade, Serbia JUG",
            date = "1992.11.04",
            round = "29",
            white = "Fischer, Robert J.",
            black = "Spassky, Boris V.",
            result = "",
            whiteElo = "",
            blackElo = "",
            timeControl = "",
            link = "",
            moves = listOf(),
        )

        // WHEN
        val pgnParser = PgnParser()
        val parsed = pgnParser.parsePgn(pgn)


        // THEN
        val result = parsed[0]
        Assertions.assertEquals(match.event, result.event)
        Assertions.assertEquals(match.site, result.site)
        Assertions.assertEquals(match.date, result.date)
        Assertions.assertEquals(match.round, result.round)
        Assertions.assertEquals(match.white, result.white)
        Assertions.assertEquals(match.black, result.black)
        Assertions.assertEquals(match.result, result.result)
        Assertions.assertEquals(match.whiteElo, result.whiteElo)
        Assertions.assertEquals(match.blackElo, result.blackElo)
        Assertions.assertEquals(match.timeControl, result.timeControl)
        Assertions.assertEquals(match.link, result.link)
    }

    @Test
    fun `test 2`() {
        // GIVEN
        val pgn = getPgn("Gukesch_Ding_2024_FWC_1.pgn")

        val match = Match(
            event = "2024 FIDE World Championship",
            site = "Chess.com",
            date = "2024.11.25",
            round = "01",
            white = "Gukesh D",
            black = "Ding, Liren",
            result = "0-1",
            whiteElo = "2783",
            blackElo = "2728",
            timeControl = "40/7200:1800+30",
            link = "https://www.chess.com/events/2024-fide-chess-world-championship/01/Gukesh_D-Ding_Liren",
            moves = listOf(),
        )

        // WHEN
        val pgnParser = PgnParser()
        val parsed = pgnParser.parsePgn(pgn)


        // THEN
        val result = parsed[0]
        Assertions.assertEquals(match.event, result.event)
        Assertions.assertEquals(match.site, result.site)
        Assertions.assertEquals(match.date, result.date)
        Assertions.assertEquals(match.round, result.round)
        Assertions.assertEquals(match.white, result.white)
        Assertions.assertEquals(match.black, result.black)
        Assertions.assertEquals(match.result, result.result)
        Assertions.assertEquals(match.whiteElo, result.whiteElo)
        Assertions.assertEquals(match.blackElo, result.blackElo)
        Assertions.assertEquals(match.timeControl, result.timeControl)
        Assertions.assertEquals(match.link, result.link)
    }

    fun getPgn(filename: String): String {
        val resource: Resource = ClassPathResource("pgns/$filename")
        val inputStream: InputStream = resource.inputStream
        return String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
    }

}