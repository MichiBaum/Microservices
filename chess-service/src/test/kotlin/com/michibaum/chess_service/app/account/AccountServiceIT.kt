package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.domain.AccountProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AccountServiceIT {

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Test
    fun `findById if saved account exists`(){
        // GIVEN
        val account = AccountProvider.account()
        val savedAccount = accountRepository.save(account)

        // WHEN
        val result = accountService.findById(savedAccount.id)

        // THEN
        assertNotNull(result)
    }

    @Test
    fun `findById if saved account does not exists`(){
        // GIVEN
        val account = AccountProvider.account()

        // WHEN
        val result = accountService.findById(account.id)

        // THEN
        assertNull(result)
    }

}