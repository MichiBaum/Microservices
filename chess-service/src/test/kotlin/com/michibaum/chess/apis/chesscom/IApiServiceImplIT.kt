package com.michibaum.chess.apis.chesscom

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.michibaum.chess.apis.IApiService
import com.michibaum.chess.apis.Success
import com.michibaum.chess.apis.dtos.TopAccountDto
import com.michibaum.chess.chesscomMockserverJson
import com.michibaum.chess.domain.AccountProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = [
    "chess-apis.properties.chesscom.base-url=http://localhost:8099"
])
class IApiServiceImplIT {

    @Autowired
    lateinit var chesscomApiService: IApiService

    companion object {
        @RegisterExtension
        var wireMockServer: WireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8099))
            .build()
    }

    @ParameterizedTest
    @ValueSource(strings = ["ezgat", "janistantv"])
    fun `get userdata`(username: String){
        // GIVEN
        val json = chesscomMockserverJson("player_$username.json")
        wireMockServer.stubFor(
            WireMock.get("/pub/player/$username")
                .willReturn(WireMock.okJson(json))
        )

        // WHEN
        val result = chesscomApiService.findUser(username)

        // THEN
        assertTrue(result is Success)
        assertEquals(username, (result as Success).result.username)
    }

    @ParameterizedTest
    @ValueSource(strings = ["ezgat", "janistantv"])
    fun `get games`(username: String){
        // GIVEN
        val account = AccountProvider.account().copy(username = username)
        val gamesJson = chesscomMockserverJson("player_${username}_games.json")
        wireMockServer.stubFor(
            WireMock.get("/pub/player/$username/games/2024/09")
                .atPriority(1) // Highest
                .willReturn(WireMock.okJson(gamesJson))
        )
        val emptyJson = chesscomMockserverJson("player_empty_games.json")
        wireMockServer.stubFor(
            WireMock.get(urlPathMatching("/pub/player/$username/games/[0-9]+/[0-9]+"))
                .atPriority(2)
                .willReturn(WireMock.okJson(emptyJson))
        )

        // WHEN
        val result = chesscomApiService.getGames(account)

        // THEN
        assertTrue(result is Success)
        assertTrue((result as Success).result.size > 1)
    }

    @ParameterizedTest
    @ValueSource(strings = ["ezgat", "janistantv"])
    fun `get stats`(username: String){
        // GIVEN
        val account = AccountProvider.account().copy(username = username)
        val json = chesscomMockserverJson("player_${username}_stats.json")
        wireMockServer.stubFor(
            WireMock.get("/pub/player/$username/stats")
                .willReturn(WireMock.okJson(json))
        )

        // WHEN
        val result = chesscomApiService.getStats(account)

        // THEN
        assertTrue(result is Success)
    }

    @Test
    fun `get leaderboard`(){
        // GIVEN
        val json = chesscomMockserverJson("leaderboards.json")
        wireMockServer.stubFor(
            WireMock.get("/pub/leaderboards")
                .willReturn(WireMock.okJson(json))
        )

        // WHEN
        val result = chesscomApiService.findTopAccounts()

        // THEN
        assertTrue(result is Success)
        val castedResult = result as Success
        assertEquals(150, castedResult.result.size)
    }

}