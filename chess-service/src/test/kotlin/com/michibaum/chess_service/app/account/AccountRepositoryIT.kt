package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.app.person.PersonRepository
import com.michibaum.chess_service.domain.AccountProvider
import com.michibaum.chess_service.domain.PersonProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestcontainersConfiguration
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
        assertEquals(account.name, result.name)
        assertEquals(account.platformId, result.platformId)
        assertEquals(account.username, result.username)
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
        assertNotNull(result.id)

        assertNotNull(result.person)
    }

}