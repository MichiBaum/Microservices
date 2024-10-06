package com.michibaum.chess.app

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = [
    "chess-apis.properties.chesscom.base-url=https://api.chess.com",
    "chess-apis.properties.lichess.base-url=https://lichess.org"
])
class AccountServiceIT {

    @Autowired
    lateinit var accountService: AccountService

    @ParameterizedTest
    @ValueSource(strings = ["ezgat", "MichiBaum"])
    fun `find accounts`(account: String){
        // GIVEN

        // WHEN
        val result = accountService.getAccounts(account)

        // THEN
        assertEquals(1, result.size)
        assertEquals(account, result[0].username)
    }

}