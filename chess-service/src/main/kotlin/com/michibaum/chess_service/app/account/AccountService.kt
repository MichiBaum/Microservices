package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.AccountRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val accountSearches: List<AccountSearch>
) {

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun saveAll(accounts: List<Account>): List<Account> {
        // TODO maybe batches of 1000??
        return accountRepository.saveAll(accounts)
    }

    fun getAccounts(username: String, local: Boolean): List<Account> {
        return accountSearches.filter { it.responsibleFor(local) }
            .flatMap { it.search(username) }
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

}


