package com.michibaum.chess.app

import com.michibaum.chess.domain.AccountProvider
import com.michibaum.chess.domain.PlayerProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
class AccountRepositoryIT {

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var playerRepository: PlayerRepository

    @Test
    fun `insert account without player`(){
        // GIVEN
        val account = AccountProvider.account()

        // WHEN
        var result = accountRepository.save(account)

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
        val player = PlayerProvider.player()
        val account = AccountProvider.account()

        // WHEN
        val savedPlayer = playerRepository.save(player)
        val accWithPlayer = account.copy(person = savedPlayer)
        val result = accountRepository.save(accWithPlayer)

        // THEN
        assertNotNull(savedPlayer.id)
        assertNotEquals(player.id, savedPlayer.id)

        assertNotNull(result.id)
        assertEquals(account.id, accWithPlayer.id)
        assertNotEquals(account.id, result.id)

        assertNotNull(result.person)

        assertNotEquals(player.id, result.person?.id)
        assertEquals(savedPlayer.id, result.person?.id)

    }

}