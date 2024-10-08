package com.michibaum.chess.app

import com.michibaum.chess.domain.AccountProvider
import com.michibaum.chess.domain.PlayerProvider
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class PersonServiceUT{

    private val personRepository: PersonRepository = mockk()
    private val personService: PersonService = PersonService(personRepository)

    @Test
    fun `connect first account`(){
        // GIVEN
        val player = PlayerProvider.player()
        val account = AccountProvider.account()

        every { personRepository.save(any()) } returnsArgument 0

        // WHEN
        val result = personService.connectAccount(player, account)

        // THEN
        Assertions.assertEquals(1, result.accounts.size)
    }

    @Test
    fun `connect second account`(){
        // GIVEN
        val account = AccountProvider.account()
        val player = PlayerProvider.player().copy(accounts = setOf(account))
        val secondAccount = AccountProvider.account()

        every { personRepository.save(any()) } returnsArgument 0

        // WHEN
        val result = personService.connectAccount(player, secondAccount)

        // THEN
        Assertions.assertEquals(2, result.accounts.size)
    }

    @Test
    fun `connect multiple account`(){
        // GIVEN
        val player = PlayerProvider.player()
        val account = AccountProvider.account()
        val secondAccount = AccountProvider.account()

        every { personRepository.save(any()) } returnsArgument 0

        // WHEN
        val result = personService.connectAccounts(player, listOf(account, secondAccount))

        // THEN
        Assertions.assertEquals(2, result.accounts.size)
    }

    @Test
    fun `connect multiple account when one is connected`(){
        // GIVEN
        val player = PlayerProvider.player().copy(accounts = setOf(AccountProvider.account()))
        val account = AccountProvider.account()
        val secondAccount = AccountProvider.account()

        every { personRepository.save(any()) } returnsArgument 0

        // WHEN
        val result = personService.connectAccounts(player, listOf(account, secondAccount))

        // THEN
        Assertions.assertEquals(3, result.accounts.size)
    }

}