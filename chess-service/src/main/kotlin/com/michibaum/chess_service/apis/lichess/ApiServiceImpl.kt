package com.michibaum.chess_service.apis.lichess

import com.michibaum.chess_service.apis.*
import com.michibaum.chess_service.apis.config.ChessConfigProperties
import com.michibaum.chess_service.apis.dtos.AccountDto
import com.michibaum.chess_service.apis.dtos.GameDto
import com.michibaum.chess_service.apis.dtos.StatsDto
import com.michibaum.chess_service.apis.dtos.TopAccountDto
import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.ChessPlatform
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient

@Service("lichessApiService")
class ApiServiceImpl(
    chessConfigProperties: ChessConfigProperties,
    restClientBuilder: RestClient.Builder,
    private val converter: Converter
): IApiService {

    val client = chessConfigProperties.createRestClient(restClientBuilder, ChessPlatform.LICHESS)

    override fun findUser(username: String): ApiResult<AccountDto> {
        val result = try {
            client.get()
                .uri("/api/user/{0}", username)
                .attributes {
                    it["pgnInJson"] = true
                }
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .body(LichessAccountDto::class.java)
        } catch (ex: HttpClientErrorException.NotFound){
            return Error("Could not find user on lichess with username=$username")
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
                .body(LichessStatsDto::class.java)
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
            val typeRef: ParameterizedTypeReference<List<LichessGameDto>> =
                object : ParameterizedTypeReference<List<LichessGameDto>>() {}
            client.get()
                .uri(url, account.username)
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .body(typeRef)
        } catch (throwable: Throwable) {
            return Exception("Exception lichess getGames with username=${account.username}", throwable)
        }

        if (result == null)
            return Error("")

        val gameDtos = result.map { converter.convert(it) }
        return Success(gameDtos)
    }

    override fun findTopAccounts(): ApiResult<List<TopAccountDto>> {
        val result = try {
            client.get()
                .uri("/api/player")
                .accept(MediaType.APPLICATION_NDJSON)
                .retrieve()
                .body(LichessLeaderboards::class.java)
        } catch (throwable: Throwable) {
            return Exception("Exception lichess findTopAccounts", throwable)
        }

        if(result == null)
            return Error("")

        val topAccountDto = result.let { converter.convert(it) }
        return Success(topAccountDto)
    }

}