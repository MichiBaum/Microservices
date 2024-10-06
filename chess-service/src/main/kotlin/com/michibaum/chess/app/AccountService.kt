package com.michibaum.chess.app

import com.michibaum.chess.apis.ApiService
import com.michibaum.chess.apis.Loggable
import com.michibaum.chess.apis.Success
import com.michibaum.chess.apis.dtos.AccountDto
import com.michibaum.chess.domain.Account
import org.springframework.stereotype.Service

@Service
class AccountService(
    private val apiService: ApiService,
    private val accountRepository: AccountRepository,
) {

    fun getAccounts(username: String): List<Account> {
        val apiResults = apiService.findAccount(username)

        // Handle errors
        apiResults.filterIsInstance<Loggable>()
            .forEach { it.log() }

        val accounts = apiResults.filterIsInstance<Success<AccountDto>>()
            .map { it.result }
            .map { it.toAccount() }
        return accountRepository.saveAll(accounts)
    }

}