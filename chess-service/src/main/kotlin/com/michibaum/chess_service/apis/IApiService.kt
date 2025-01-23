package com.michibaum.chess_service.apis

import com.michibaum.chess_service.apis.dtos.AccountDto
import com.michibaum.chess_service.apis.dtos.GameDto
import com.michibaum.chess_service.apis.dtos.StatsDto
import com.michibaum.chess_service.apis.dtos.TopAccountDto
import com.michibaum.chess_service.database.Account

interface IApiService {

    fun findUser(username: String): ApiResult<AccountDto>

    fun getStats(account: Account): ApiResult<StatsDto>

    fun getGames(account: Account): ApiResult<List<GameDto>>
    fun findTopAccounts(): ApiResult<List<TopAccountDto>>

}