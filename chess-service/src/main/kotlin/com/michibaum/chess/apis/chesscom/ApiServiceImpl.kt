package com.michibaum.chess.apis.chesscom

import com.michibaum.chess.apis.*
import com.michibaum.chess.apis.config.ChessConfigProperties
import com.michibaum.chess.apis.dtos.AccountDto
import com.michibaum.chess.apis.dtos.GameDto
import com.michibaum.chess.apis.dtos.StatsDto
import com.michibaum.chess.domain.Account
import com.michibaum.chess.domain.ChessPlatform
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.*


@Service(value = "chesscomApiService")
class ApiServiceImpl(
    chessConfigProperties: ChessConfigProperties,
    val converter: Converter
): IApiService {

    val client = chessConfigProperties.getWebClient(ChessPlatform.CHESSCOM)

    override fun findUser(username: String): ApiResult<AccountDto> {
        val result = try {
             client.get()
                .uri("/pub/player/{0}", username)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ChesscomAccountDto::class.java) // TODO onError
                .block()
        } catch (throwable: Throwable){
            return Exception(throwable)
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
                .bodyToMono(ChesscomStatsDto::class.java) // TODO onError
                .block()
        } catch (throwable: Throwable){
            return Exception(throwable)
        }

        if(result == null)
            return Error("")

        val statsDto = result.let { converter.convert(it) }
        return Success(statsDto)
    }

    override fun getGames(account: Account): ApiResult<List<GameDto>> { // TODO Year and month
        val creationYear = run {
            val calendar = Calendar.getInstance()
            calendar.time = account.createdAt
            calendar.get(Calendar.YEAR);
        }

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
                        .bodyToMono(Gamesresult::class.java)
                        .mapNotNull { it.games }
                        .block()
                } catch (throwable: Throwable){
                    return Exception(throwable)
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

    private fun getMonth(month: Int): String =
        if (month.toString().length == 1) {
            "0$month"
        } else {
            month.toString()
        }

}