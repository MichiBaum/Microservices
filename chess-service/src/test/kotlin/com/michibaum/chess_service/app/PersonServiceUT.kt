package com.michibaum.chess_service.app

import com.michibaum.chess_service.domain.AccountProvider
import com.michibaum.chess_service.domain.PersonProvider
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
        val person = PersonProvider.person()
        val account = AccountProvider.account()

        every { personRepository.save(any()) } returnsArgument 0

        // WHEN
        val result = personService.connectAccount(person, account)

        // THEN
        Assertions.assertEquals(1, result.accounts.size)
    }

    @Test
    fun `connect second account`(){
        // GIVEN
        val account = AccountProvider.account()
        val person = PersonProvider.person(setOf(account))
        val secondAccount = AccountProvider.account()

        every { personRepository.save(any()) } returnsArgument 0

        // WHEN
        val result = personService.connectAccount(person, secondAccount)

        // THEN
        Assertions.assertEquals(2, result.accounts.size)
    }

    @Test
    fun `connect multiple account`(){
        // GIVEN
        val person = PersonProvider.person()
        val account = AccountProvider.account()
        val secondAccount = AccountProvider.account()

        every { personRepository.save(any()) } returnsArgument 0

        // WHEN
        val result = personService.connectAccounts(person, listOf(account, secondAccount))

        // THEN
        Assertions.assertEquals(2, result.accounts.size)
    }

    @Test
    fun `connect multiple account when one is connected`(){
        // GIVEN
        val account = AccountProvider.account()
        val secondAccount = AccountProvider.account()
        val person = PersonProvider.person(setOf(account, secondAccount))

        val additionalAccount = AccountProvider.account()
        val additionalSecondAccount = AccountProvider.account()

        every { personRepository.save(any()) } returnsArgument 0

        // WHEN
        val result = personService.connectAccounts(person, listOf(additionalAccount, additionalSecondAccount))

        // THEN
        Assertions.assertEquals(4, result.accounts.size)
    }

}