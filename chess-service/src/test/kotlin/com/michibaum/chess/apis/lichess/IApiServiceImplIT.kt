package com.michibaum.chess.apis.lichess

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.michibaum.chess.apis.IApiService
import com.michibaum.chess.apis.Success
import com.michibaum.chess.apis.chesscom.IApiServiceImplIT
import com.michibaum.chess.apis.chesscom.IApiServiceImplIT.Companion
import com.michibaum.chess.chesscomMockserverJson
import com.michibaum.chess.domain.AccountProvider
import com.michibaum.chess.lichessMockserverJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest(properties = [
"chess-apis.properties.lichess.base-url=http://localhost:8098"
])
class IApiServiceImplIT{
    @Autowired
    lateinit var lichessApiService: IApiService

    companion object {
        @RegisterExtension
        var wireMockServer: WireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(8098))
            .build()
    }

    @ParameterizedTest
    @ValueSource(strings = ["MichiBaum", "JanistanTV"])
    fun `get userdata`(username: String){
        // GIVEN
        val json = lichessMockserverJson("api_user_$username.json")
        wireMockServer.stubFor(
            WireMock.get("/api/user/$username")
                .willReturn(WireMock.okJson(json))
        )

        // WHEN
        val result = lichessApiService.findUser(username)

        // THEN
        assertTrue(result is Success)
        assertEquals(username, (result as Success).result.username)
    }

    @ParameterizedTest
    @ValueSource(strings = ["MichiBaum", "JanistanTV"])
    fun `get games`(username: String){
        // GIVEN
        val account = AccountProvider.account().copy(username = username)
        val json = lichessMockserverJson("api_games_user_$username.json")
        wireMockServer.stubFor(
            WireMock.get("/api/games/user/$username")
                .willReturn(WireMock.okJson(json))
        )

        // WHEN
        val result = lichessApiService.getGames(account)

        // THEN
        assertTrue(result is Success)
        assertTrue((result as Success).result.size > 1)

    }

    @ParameterizedTest
    @ValueSource(strings = ["MichiBaum", "JanistanTV"])
    fun `get stats`(username: String){
        // GIVEN
        val account = AccountProvider.account().copy(username = username)

        val jsonBullet = lichessMockserverJson("api_user_${username}_perf_bullet.json")
        wireMockServer.stubFor(
            WireMock.get("/api/user/$username/perf/bullet")
                .willReturn(WireMock.okJson(jsonBullet))

        )
        val jsonBlitz = lichessMockserverJson("api_user_${username}_perf_blitz.json")
        wireMockServer.stubFor(
            WireMock.get("/api/user/$username/perf/blitz")
                .willReturn(WireMock.okJson(jsonBlitz))

        )
        val jsonRapid = lichessMockserverJson("api_user_${username}_perf_rapid.json")
        wireMockServer.stubFor(
            WireMock.get("/api/user/$username/perf/rapid")
                .willReturn(WireMock.okJson(jsonRapid))

        )

        // WHEN
        val result = lichessApiService.getStats(account)

        // THEN
        assertTrue(result is Success)
    }

    @Test
    fun `get leaderboard`(){
        // GIVEN
        val json = lichessMockserverJson("api_player.json")
        wireMockServer.stubFor(
            WireMock.get("/api/player")
                .willReturn(WireMock.okJson(json))
        )

        // WHEN
        val result = lichessApiService.findTopAccounts()

        // THEN
        assertTrue(result is Success)
    }

}