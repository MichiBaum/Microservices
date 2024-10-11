package com.michibaum.chess_service.app

import com.michibaum.chess_service.domain.AccountProvider
import com.michibaum.chess_service.domain.PersonProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class PersonRepositoryIT {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    @Test
    fun `insert player without accounts`(){
        // GIVEN
        val player = PersonProvider.person()

        // WHEN
        val result = personRepository.save(player)

        // THEN
        assertNotNull(result.id)
        assertEquals(player.firstname, result.firstname)
        assertEquals(player.lastname, result.lastname)
        assertNotNull(result.accounts)
        assertEquals(0, result.accounts.size)
    }

    @Test
    fun `insert player with account`(){
        // GIVEN
        val player = PersonProvider.person()
        val account = AccountProvider.account()

        // WHEN
        val savedAccount = accountRepository.save(account)
        val playerWithAccount = player //.copy(accounts = player.accounts + savedAccount)
        val result = personRepository.save(playerWithAccount)

        // THEN
        assertNotNull(account.id)
        assertNotNull(savedAccount.id)
        assertNotEquals(account.id, savedAccount.id)

        assertNotNull(playerWithAccount.id)
        assertNotNull(result.id)
        assertNotEquals(playerWithAccount.id, result.id)

        assertEquals(1, result.accounts.size)
        assertEquals(savedAccount.id, result.accounts.elementAt(0).id)
    }

}