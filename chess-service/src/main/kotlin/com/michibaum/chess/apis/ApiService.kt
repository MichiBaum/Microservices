package com.michibaum.chess.apis

import com.michibaum.chess.apis.dtos.AccountDto
import com.michibaum.chess.apis.dtos.GameDto
import com.michibaum.chess.apis.dtos.StatsDto
import com.michibaum.chess.doIfIsInstance
import com.michibaum.chess.domain.Account
import com.michibaum.chess.domain.ChessPlatform
import org.springframework.stereotype.Service

@Service
class ApiService(
    val chesscomApiService: IApiService,
    val lichessApiService: IApiService
) {

    fun findAccount(username: String): List<ApiResult<AccountDto>> {
        val chesscomUser =  chesscomApiService.findUser(username)
        val lichessUser =  lichessApiService.findUser(username)

        return listOf(chesscomUser, lichessUser)

    }

    fun getStats(account: Account): ApiResult<StatsDto> {
        return when (account.platform) {
            ChessPlatform.CHESSCOM -> chesscomApiService.getStats(account)
            ChessPlatform.LICHESS -> lichessApiService.getStats(account)
        }

    }

    fun getGames(account: Account): List<GameDto> {
        val result = when(account.platform) {
            ChessPlatform.CHESSCOM -> chesscomApiService.getGames(account)
            ChessPlatform.LICHESS -> lichessApiService.getGames(account)
        }

        if(result is Success){
            return result.result
        }
        return emptyList()
    }

    fun getTopAccounts(): List<AccountDto> {
        val chesscomTopAccounts = chesscomApiService.findTopAccounts()
        val chesscomAccounts = when(chesscomTopAccounts){
            is Success -> Success(chesscomTopAccounts.result.map { chesscomApiService.findUser(it.username) })
            else -> Error("")
        }

        val lichessTopAccounts = lichessApiService.findTopAccounts()
        val lichessAccounts = when(lichessTopAccounts){
            is Success -> Success(lichessTopAccounts.result.map { lichessApiService.findUser(it.username) })
            else -> Error("")
        }

        val allResults = listOf(chesscomAccounts, lichessAccounts)

        return allResults
            .doIfIsInstance<ApiResult<List<ApiResult<AccountDto>>>, Loggable> { it.log() }
            .filterIsInstance<Success<List<ApiResult<AccountDto>>>>()
            .map { it.result }
            .flatten()
            .doIfIsInstance<ApiResult<AccountDto>, Loggable> { it.log() }
            .filterIsInstance<Success<AccountDto>>()
            .map { it.result }
    }


}