package com.michibaum.chess.app

import com.michibaum.chess.domain.AccountProvider
import com.michibaum.chess.domain.PlayerProvider
import com.michibaum.chess.get
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class PersonRepositoryIT {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var playerRepository: PlayerRepository

    @Test
    fun `insert player without accounts`(){
        // GIVEN
        val player = PlayerProvider.player()

        // WHEN
        var result = playerRepository.save(player)

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
        val player = PlayerProvider.player()
        val account = AccountProvider.account()

        // WHEN
        val savedAccount = accountRepository.save(account)
        val playerWithAccount = player.copy(accounts = player.accounts + savedAccount)
        val result = playerRepository.save(playerWithAccount)

        // THEN
        assertNotNull(account.id)
        assertNotNull(savedAccount.id)
        assertNotEquals(account.id, savedAccount.id)

        assertNotNull(playerWithAccount.id)
        assertNotNull(result.id)
        assertNotEquals(playerWithAccount.id, result.id)

        assertEquals(1, result.accounts.size)
        assertEquals(savedAccount.id, result.accounts[0].id)
    }

}