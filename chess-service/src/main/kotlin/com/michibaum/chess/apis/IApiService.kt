package com.michibaum.chess.apis

import com.michibaum.chess.apis.dtos.AccountDto
import com.michibaum.chess.apis.dtos.GameDto
import com.michibaum.chess.apis.dtos.StatsDto
import com.michibaum.chess.apis.dtos.TopAccountDto
import com.michibaum.chess.domain.Account

interface IApiService {

    fun findUser(username: String): ApiResult<AccountDto>

    fun getStats(account: Account): ApiResult<StatsDto>

    fun getGames(account: Account): ApiResult<List<GameDto>>
    fun findTopAccounts(): ApiResult<List<TopAccountDto>>

}