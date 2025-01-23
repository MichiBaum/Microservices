package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.apis.ApiResult
import com.michibaum.chess_service.apis.ApiService
import com.michibaum.chess_service.apis.Loggable
import com.michibaum.chess_service.apis.Success
import com.michibaum.chess_service.apis.dtos.AccountDto
import com.michibaum.chess_service.doIfIsInstance
import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.AccountRepository
import com.michibaum.chess_service.database.ChessPlatform
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class AccountService(
    private val apiService: ApiService,
    private val accountRepository: AccountRepository,
) {

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun saveAll(accounts: List<Account>): List<Account> {
        // TODO maybe batches of 1000??
        return accountRepository.saveAll(accounts)
    }

    fun getAccounts(username: String, local: Boolean): List<Account> {
        if(!local){
            val results = searchAccountOnApis(username)
            return results // TODO maybe not save yet?
        }
        return accountRepository.findByUsernameContainingIgnoreCase(username)
    }

    fun searchAccountOnApis(username: String): ArrayList<Account> {
        val apiResults = apiService.findAccount(username)

        val foundAccounts = apiResults
            .doIfIsInstance<ApiResult<AccountDto>, Loggable> { it.log() }
            .filterIsInstance<Success<AccountDto>>()
            .map { it.result }

        val newAccounts = ArrayList<Account>()
        for(account in foundAccounts) {
            val db = accountRepository.findByPlatformAndPlatformId(account.platform, account.id)
            val newAccount = if(db != null)
                update(db, account)
            else
                account.toAccount()
            newAccounts.add(newAccount)
        }
        return newAccounts
    }

//    fun getTopAccounts(): List<Account>{
//        val apiResults = apiService.getTopAccounts()
//
//        // Handle errors
//        apiResults.filterIsInstance<Loggable>()
//            .forEach { it.log() }
//
//        val accounts = apiResults.filterIsInstance<Success<AccountDto>>()
//            .map { it.result }
//            .filter { !accountRepository.existsByPlatformIdAndUsername(it.id, it.username) }
//            .map { it.toAccount() }
//        return accountRepository.saveAll(accounts)
//
//    }

    fun findByAccountId(accountId: UUID): Account? {
        return accountRepository.findById(accountId).getOrNull()
    }

    fun createOrUpdate(platform: ChessPlatform, apiAccounts: List<AccountDto>): List<Account> {
        val existingAccounts = accountRepository.findAllByPlatformAndPlatformIdIn(platform, apiAccounts.map { it.id })
        val newAccounts = ArrayList<Account>()
        for (apiAccount in apiAccounts) {
            val existing = existingAccounts.find { it.platformId == apiAccount.id }
            val newAccount = if(existing != null)
                update(existing, apiAccount)
            else
                apiAccount.toAccount()
            newAccounts.add(newAccount)
        }
        return newAccounts
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


