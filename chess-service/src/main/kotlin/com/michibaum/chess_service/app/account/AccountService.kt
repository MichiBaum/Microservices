package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.AccountRepository
import com.michibaum.chess_service.database.PersonRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Service
class AccountService(
    private val accountRepository: AccountRepository,
    private val personRepository: PersonRepository,
    private val accountSearches: List<AccountSearch>
) {

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun saveAll(accounts: List<Account>): List<Account> {
        // TODO maybe batches of 1000??
        return accountRepository.saveAll(accounts)
    }

    fun getAccounts(searchAccountDto: SearchAccountDto): List<Account> {
        return accountSearches.filter { it.responsibleFor(searchAccountDto.searchLocation) }
            .flatMap { it.search(searchAccountDto) }
    }

    fun findByAccountId(accountId: UUID): Account? {
        return accountRepository.findById(accountId).getOrNull()
    }

    fun create(dto: WriteAccountDto): Account {
        val person = dto.personId?.let { personRepository.findById(UUID.fromString(it)).getOrNull() }
        val account = Account(
            platformId = dto.platformId,
            name = dto.name,
            username = dto.username,
            platform = dto.platform,
            createdAt = dto.createdAt?.let { LocalDate.parse(it) },
            person = person
        )
        return accountRepository.save(account)
    }

    fun update(account: Account, dto: WriteAccountDto): Account {
        val person = dto.personId?.let { personRepository.findById(UUID.fromString(it)).getOrNull() }
        val newAccount = Account(
            platformId = dto.platformId,
            name = dto.name,
            username = dto.username,
            platform = dto.platform,
            createdAt = dto.createdAt?.let { LocalDate.parse(it) },
            person = person,
            id = account.id
        )
        return accountRepository.save(newAccount)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false, isolation = Isolation.REPEATABLE_READ)
    fun delete(account: Account) {
        accountRepository.delete(account)
    }

}


