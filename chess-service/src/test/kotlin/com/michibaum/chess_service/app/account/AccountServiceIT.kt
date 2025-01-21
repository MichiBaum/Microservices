package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.AccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestcontainersConfiguration
class AccountServiceIT {

    @Autowired
    lateinit var accountService: AccountService

    @Autowired
    lateinit var accountRepository: AccountRepository


//    @Test
//    fun `findById if saved account exists`(){
//        // GIVEN
//        val account = AccountProvider.account()
//        val savedAccount = accountRepository.save(account)
//
//        // WHEN
//        val result = accountService.findByAccountId(savedAccount.idOrThrow())
//
//        // THEN
//        assertNotNull(result)
//    }
//
//    @Test
//    fun `findById if saved account does not exists`(){
//        // GIVEN
//        val id = UUID.randomUUID()
//
//        // WHEN
//        val result = accountService.findByAccountId(id)
//
//        // THEN
//        assertNull(result)
//    }

}