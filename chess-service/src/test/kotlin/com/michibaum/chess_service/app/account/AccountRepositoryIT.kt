package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.app.person.PersonRepository
import com.michibaum.chess_service.domain.AccountProvider
import com.michibaum.chess_service.domain.PersonProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class AccountRepositoryIT {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    @Test
    fun `insert account without player`(){
        // GIVEN
        val account = AccountProvider.account()

        // WHEN
        val result = accountRepository.save(account)

        // THEN
        assertNotNull(account.id)
        assertNotNull(result.id)
        assertNotEquals(account.id, result.id)
        assertEquals(account.name, result.name)
        assertEquals(account.accId, result.accId)
        assertEquals(account.username, result.username)
        assertEquals(account.url, result.url)
        assertEquals(account.platform, result.platform)
        assertEquals(account.person, result.person)
    }

    @Test
    fun `insert account with player`(){
        // GIVEN
        val personToSave = PersonProvider.person()

        // WHEN
        val person = personRepository.save(personToSave)
        val account = AccountProvider.account(person = person)
        val result = accountRepository.save(account)

        // THEN
        assertNotNull(person.id)
        assertNotEquals(personToSave.id, person.id)

        assertNotNull(result.id)
        assertEquals(account.id, account.id)
        assertNotEquals(account.id, result.id)

        assertNotNull(result.person)

        assertNotEquals(personToSave.id, result.person?.id)
        assertEquals(person.id, result.person?.id)

    }

}