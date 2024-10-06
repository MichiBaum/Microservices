package com.michibaum.chess.apis

import com.michibaum.chess.apis.dtos.AccountDto
import com.michibaum.chess.apis.dtos.StatsDto
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


}