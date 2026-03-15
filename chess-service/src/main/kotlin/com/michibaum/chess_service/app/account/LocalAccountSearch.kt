package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.AccountRepository
import org.springframework.stereotype.Component

@Component
class LocalAccountSearch(
    private val accountRepository: AccountRepository,
): AccountSearch {
    
    override fun responsibleFor(local: Boolean): Boolean {
        return local
    }

    override fun search(query: String): List<Account> {
        return accountRepository.findByUsernameContainingIgnoreCase(query)
    }
}