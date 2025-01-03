package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.domain.AccountProvider
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@TestcontainersConfiguration
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
        val result = accountService.findById(savedAccount.idOrThrow())
        println(accountRepository.count())

        // THEN
        assertNotNull(result)
    }

    @Test
    fun `findById if saved account does not exists`(){
        // GIVEN
        val id = UUID.randomUUID()

        // WHEN
        val result = accountService.findById(id)
        println(accountRepository.count())

        // THEN
        assertNull(result)
    }

}