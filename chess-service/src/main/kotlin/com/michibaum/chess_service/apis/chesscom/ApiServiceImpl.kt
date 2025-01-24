package com.michibaum.chess_service.apis.chesscom

import com.michibaum.chess_service.apis.*
import com.michibaum.chess_service.apis.config.ChessConfigProperties
import com.michibaum.chess_service.apis.dtos.AccountDto
import com.michibaum.chess_service.apis.dtos.GameDto
import com.michibaum.chess_service.apis.dtos.StatsDto
import com.michibaum.chess_service.apis.dtos.TopAccountDto
import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.ChessPlatform
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter


@Service(value = "chesscomApiService")
class ApiServiceImpl(
    chessConfigProperties: ChessConfigProperties,
    private val converter: Converter
): IApiService {

    val client = chessConfigProperties.getWebClient(ChessPlatform.CHESSCOM)

    override fun findUser(username: String): ApiResult<AccountDto> {
        val result = try {
            client.get()
                .uri("/pub/player/{0}", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ChesscomAccountDto::class.java) // TODO onError
        } catch (ex: HttpClientErrorException.NotFound){
            return Error("Could not find user on chess.com with username=$username")
        } catch (throwable: Throwable){
            return Exception("Exception chess.com findUser with username=$username", throwable)
        }

        if(result == null)
            return Error("")

        val accountDto = result.let { converter.convert(it) }
        return Success(accountDto)
    }

    override fun getStats(account: Account): ApiResult<StatsDto> {
        val result = try {
            client.get()
                .uri("/pub/player/{0}/stats", account.username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ChesscomStatsDto::class.java) // TODO onError
        } catch (throwable: Throwable){
            return Exception("Exception chesscom getStats with username=${account.username}", throwable)
        }

        if(result == null)
            return Error("")

        val statsDto = result.let { converter.convert(it) }
        return Success(statsDto)
    }

    override fun getGames(account: Account): ApiResult<List<GameDto>> { // TODO Year and month
        val creationYear = account.createdAt?.year ?: return Error("Unknown account creation year")

        val years = IntRange(creationYear, Year.now().value)
        val months = IntRange(1, 12)

        val results = mutableListOf<MutableList<ChesscomGameDto?>?>()
        for (year in years){
            for (month in months){
                val currentMonth = getMonth(month)
                val currentYear = year.toString()

                val dateIsInTheFuture = LocalDate.now().isBefore(LocalDate.parse("$currentYear-$currentMonth-01", DateTimeFormatter.ofPattern( "yyyy-MM-dd" )))
                if(dateIsInTheFuture)
                    continue

                val result = try {
                    client.get()
                        .uri("/pub/player/{0}/games/{1}/{2}", account.username, currentYear, currentMonth)
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .body(Gamesresult::class.java)
                        ?.games
                } catch (throwable: Throwable){
                    return Exception("Exception chesscom getGames with username=${account.username} and year=$currentYear and month=$currentMonth", throwable)
                }
                results.add(result)
            }
        }

        val allResults = results.filterNotNull()
            .flatten()
            .filterNotNull()

        if(allResults.isEmpty())
            return Error("")

        val gameDtos = allResults.map { it.let { converter.convert(it) } }
        return Success(gameDtos)
    }

    override fun findTopAccounts(): ApiResult<List<TopAccountDto>> {
        val result = try {
            client.get()
                .uri("/pub/leaderboards")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ChesscomLeaderboards::class.java) // TODO onError
        } catch (throwable: Throwable){
            return Exception("Exception chesscom findTopAccounts", throwable)
        }

        if(result == null)
            return Error("Error happend while fetching chesscom top accounts")

        val topAccountDto = result.let { converter.convert(it) }
        return Success(topAccountDto)
    }

    private fun getMonth(month: Int): String =
        if (month.toString().length == 1) {
            "0$month"
        } else {
            month.toString()
        }

}