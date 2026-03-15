package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.AccountRepository
import org.springframework.stereotype.Component

@Component
class LocalAccountSearch(
    private val accountRepository: AccountRepository,
): AccountSearch {
    
    override fun responsibleFor(searchLocation: SearchLocation): Boolean {
        return searchLocation == SearchLocation.LOCAL
    }

    override fun search(searchAccountDto: SearchAccountDto): List<Account> {
        return accountRepository.findAll(searchAccountDto.getSpecification())
    }
}