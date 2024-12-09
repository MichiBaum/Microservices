package com.michibaum.chess_service.apis

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(properties = [
    "chess-apis.properties.lichess.base-url=https://lichess.org",
    "chess-apis.properties.chesscom.base-url=https://api.chess.com"
])
class ApiServiceIT{

    @Autowired
    lateinit var apiService: ApiService

    @Test
    @Disabled
    fun getTopAccounts(){
        // GIVEN


        // WHEN
        val result = apiService.getTopAccounts()

        // THEN
        assertNotEquals(0, result.size)
        assertEquals(200, result.size)

    }

}