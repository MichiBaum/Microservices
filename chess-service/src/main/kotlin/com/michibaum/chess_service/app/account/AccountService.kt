package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.apis.ApiResult
import com.michibaum.chess_service.apis.ApiService
import com.michibaum.chess_service.apis.Loggable
import com.michibaum.chess_service.apis.Success
import com.michibaum.chess_service.apis.dtos.AccountDto
import com.michibaum.chess_service.doIfIsInstance
import com.michibaum.chess_service.domain.Account
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@Service
class AccountService(
    private val apiService: ApiService,
    private val accountRepository: AccountRepository,
) {

    fun getAccounts(username: String): List<Account> {
        val apiResults = apiService.findAccount(username)

        val accounts = apiResults
            .doIfIsInstance<ApiResult<AccountDto>, Loggable> { it.log() }
            .filterIsInstance<Success<AccountDto>>()
            .map { it.result }
            .map { it.toAccount() }
        return accountRepository.saveAll(accounts)
    }

    fun getTopAccounts(): List<Account>{
        val apiResults = apiService.getTopAccounts()

        // Handle errors
        apiResults.filterIsInstance<Loggable>()
            .forEach { it.log() }

        val accounts = apiResults.filterIsInstance<Success<AccountDto>>()
            .map { it.result }
            .map { it.toAccount() }
        return accountRepository.saveAll(accounts)

    }

    fun findById(accountId: UUID): Account? {
        return accountRepository.findById(accountId).getOrNull()
    }

}


