package com.michibaum.chess_service.apis.fide

import com.michibaum.chess_service.apis.dtos.AccountDto
import java.io.InputStream

interface FideApiService {

    fun getAccounts(inputStream: InputStream): List<AccountDto>

}