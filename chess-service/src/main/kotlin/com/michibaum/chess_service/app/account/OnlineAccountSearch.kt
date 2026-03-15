package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.apis.ApiResult
import com.michibaum.chess_service.apis.ApiService
import com.michibaum.chess_service.apis.Loggable
import com.michibaum.chess_service.apis.Success
import com.michibaum.chess_service.apis.dtos.AccountDto
import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.AccountRepository
import com.michibaum.chess_service.doIfIsInstance
import org.springframework.stereotype.Component
import java.util.ArrayList

@Component
class OnlineAccountSearch(
    private val accountRepository: AccountRepository,
    private val apiService: ApiService,
): AccountSearch {
    override fun responsibleFor(searchLocation: SearchLocation): Boolean {
        return searchLocation == SearchLocation.ONLINE
    }

    override fun search(query: String): List<Account> {
        val apiResults = apiService.findAccount(query)

        val foundAccounts = apiResults
            .doIfIsInstance<ApiResult<AccountDto>, Loggable> { it.log() }
            .filterIsInstance<Success<AccountDto>>()
            .map { it.result }

        val accounts = ArrayList<Account>()
        for(account in foundAccounts) {
            val db = accountRepository.findByPlatformAndPlatformId(account.platform, account.id)
            val newAccount = if(db != null)
                update(db, account)
            else
                account.toAccount()
            accounts.add(newAccount)
        }
        return accounts
    }

    private fun update(it: Account, apiAccount: AccountDto): Account {
        return Account(
            platformId = it.platformId,
            name = apiAccount.name,
            username = apiAccount.username,
            platform = it.platform,
            createdAt = it.createdAt,
            person = it.person,
            id = it.id
        )
    }
}