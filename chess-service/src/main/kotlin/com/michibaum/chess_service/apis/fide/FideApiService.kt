package com.michibaum.chess_service.apis.fide

import com.michibaum.chess_service.apis.dtos.AccountDto
import com.michibaum.chess_service.apis.dtos.FidePersonDto
import java.io.InputStream

interface FideApiService {

    fun getAccounts(inputStream: InputStream): List<AccountDto>
    fun getPersons(inputStream: InputStream): List<FidePersonDto>
    
}