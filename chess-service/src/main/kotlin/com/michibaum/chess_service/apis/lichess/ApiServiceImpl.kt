package com.michibaum.chess_service.apis.lichess

import com.michibaum.chess_service.apis.*
import com.michibaum.chess_service.apis.config.ChessConfigProperties
import com.michibaum.chess_service.apis.dtos.AccountDto
import com.michibaum.chess_service.apis.dtos.GameDto
import com.michibaum.chess_service.apis.dtos.StatsDto
import com.michibaum.chess_service.apis.dtos.TopAccountDto
import com.michibaum.chess_service.domain.Account
import com.michibaum.chess_service.domain.ChessPlatform
import org.springframework.http.MediaType
import org.springframework.stereotype.Service

@Service("lichessApiService")
class ApiServiceImpl(
    chessConfigProperties: ChessConfigProperties,
    val converter: Converter
): IApiService {

    val client = chessConfigProperties.getWebClient(ChessPlatform.LICHESS)

    override fun findUser(username: String): ApiResult<AccountDto> {
        val result = try {
            client.get()
                .uri("/api/user/{0}", username)
                .attributes {
                    it["pgnInJson"] = true
                }
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToMono(LichessAccountDto::class.java)
                .block()
        } catch (throwable: Throwable) {
            return Exception("Exception lichess findUser with username=${username}", throwable)
        }

        if(result == null)
            return Error("")

        val accountDto = result.let { converter.convert(it) }
        return Success(accountDto)
    }

    override fun getStats(account: Account): ApiResult<StatsDto> {
        val statsRequest: (String) -> LichessStatsDto? = { perf: String ->
            client.get()
                .uri("/api/user/{0}/perf/{1}", account.username, perf)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(LichessStatsDto::class.java)
                .block()
        }

        val bullet = try {
            statsRequest("bullet")
        } catch (throwable: Throwable) {
            return Exception("Exception lichess getStats with username=${account.username} and perf=bullet", throwable)
        }
        val blitz = try {
            statsRequest("blitz")
        } catch (throwable: Throwable) {
            return Exception("Exception lichess getStats with username=${account.username} and perf=blitz", throwable)
        }
        val rapid = try {
            statsRequest("rapid")
        } catch (throwable: Throwable) {
            return Exception("Exception lichess getStats with username=${account.username} and perf=rapid", throwable)
        }

        if(bullet == null)
            return Error("")
        if(blitz == null)
            return Error("")
        if(rapid == null)
            return Error("")

        val statsDto = converter.convert(bullet, blitz, rapid)
        return Success(statsDto)

    }

    override fun getGames(account: Account): ApiResult<List<GameDto>> {
        val result = try {
            val url = "/api/games/user/{0}?perfType=bullet,blitz,rapid&pgnInJson=true&tags=true&clocks=true"
            client.get()
                .uri(url, account.username)
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToFlux(LichessGameDto::class.java)
                .collectList()
                .block()
        } catch (throwable: Throwable) {
            return Exception("Exception lichess getGames with username=${account.username}", throwable)
        }

        if (result == null)
            return Error("")

        val gameDtos = result.mapNotNull { converter.convert(it) }
        return Success(gameDtos)
    }

    override fun findTopAccounts(): ApiResult<List<TopAccountDto>> {
        val result = try {
            client.get()
                .uri("/api/player")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .bodyToMono(LichessLeaderboards::class.java)
                .block()
        } catch (throwable: Throwable) {
            return Exception("Exception lichess findTopAccounts", throwable)
        }

        if(result == null)
            return Error("")

        val topAccountDto = result.let { converter.convert(it) }
        return Success(topAccountDto)
    }

}