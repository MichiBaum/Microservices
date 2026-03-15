package com.michibaum.chess_service.app.account

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.Account
import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.AccountRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class AccountControllerUpdateIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should update account when data is valid`(){
        // GIVEN
        val existingAccount = Account(
            platformId = "12345",
            name = "Old Name",
            username = "olduser",
            platform = ChessPlatform.CHESSCOM,
            createdAt = null,
            person = null
        )
        val savedAccount = accountRepository.save(existingAccount)

        val updateDto = WriteAccountDto(
            platformId = "12345",
            name = "New Name",
            username = "newuser",
            platform = ChessPlatform.LICHESS,
            createdAt = "2024-01-01"
        )

        // WHEN
        mockMvc.perform(
            put("/api/accounts/${savedAccount.id}")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.id").value(savedAccount.id.toString()),
            jsonPath("$.name").value("New Name"),
            jsonPath("$.username").value("newuser"),
            jsonPath("$.platform").value("LICHESS")
        )
    }

    @Test
    fun `should return not found when updating non-existing account`(){
        // GIVEN
        val updateDto = WriteAccountDto(
            platformId = "12345",
            name = "New Name",
            username = "newuser",
            platform = ChessPlatform.LICHESS
        )
        val randomId = UUID.randomUUID()

        // WHEN
        mockMvc.perform(
            put("/api/accounts/$randomId")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isNotFound
        )
    }
}
