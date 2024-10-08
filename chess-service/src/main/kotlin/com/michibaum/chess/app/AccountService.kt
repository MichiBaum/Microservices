package com.michibaum.chess.app

import com.michibaum.chess.apis.ApiResult
import com.michibaum.chess.apis.ApiService
import com.michibaum.chess.apis.Loggable
import com.michibaum.chess.apis.Success
import com.michibaum.chess.apis.dtos.AccountDto
import com.michibaum.chess.doIfIsInstance
import com.michibaum.chess.domain.Account
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


