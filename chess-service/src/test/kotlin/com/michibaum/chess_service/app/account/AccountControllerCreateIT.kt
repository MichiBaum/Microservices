package com.michibaum.chess_service.app.account

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.chess_service.TestcontainersConfiguration
import com.michibaum.chess_service.database.ChessPlatform
import com.michibaum.chess_service.database.AccountRepository
import com.michibaum.chess_service.database.Gender
import com.michibaum.chess_service.database.Person
import com.michibaum.chess_service.database.PersonRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@AutoConfigureMockMvc
@SpringBootTest
@TestcontainersConfiguration
class AccountControllerCreateIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var accountRepository: AccountRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `should create account when data is valid`(){
        // GIVEN
        val accountDto = WriteAccountDto(
            platformId = "12345",
            name = "Test Account",
            username = "testuser",
            platform = ChessPlatform.CHESSCOM,
            createdAt = "2024-01-01"
        )

        // WHEN
        mockMvc.perform(
            post("/api/accounts")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isCreated,
            jsonPath("$.id").exists(),
            jsonPath("$.name").value("Test Account"),
            jsonPath("$.username").value("testuser"),
            jsonPath("$.platform").value("CHESSCOM")
        )
    }

    @Test
    fun `should return bad request when name is empty`(){
        // GIVEN
        val accountDto = WriteAccountDto(
            platformId = "12345",
            name = "",
            username = "testuser",
            platform = ChessPlatform.CHESSCOM
        )

        // WHEN
        mockMvc.perform(
            post("/api/accounts")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )
    }

    @Test
    fun `should return bad request when date format is invalid`(){
        // GIVEN
        val accountDto = WriteAccountDto(
            platformId = "12345",
            name = "Test Account",
            username = "testuser",
            platform = ChessPlatform.CHESSCOM,
            createdAt = "01-01-2024" // Invalid format
        )

        // WHEN
        mockMvc.perform(
            post("/api/accounts")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest
        )
    }

    @Test
    fun `should create account with personId`(){
        // GIVEN
        val person = Person(
            firstname = "John",
            lastname = "Doe",
            federation = "USA",
            birthday = null,
            gender = Gender.MALE,
            accounts = emptySet()
        )
        val savedPerson = personRepository.save(person)

        val accountDto = WriteAccountDto(
            platformId = "12345",
            name = "Test Account",
            username = "testuser",
            platform = ChessPlatform.CHESSCOM,
            personId = savedPerson.id.toString()
        )

        // WHEN
        mockMvc.perform(
            post("/api/accounts")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isCreated,
            jsonPath("$.id").exists(),
            jsonPath("$.person.id").value(savedPerson.id.toString()),
            jsonPath("$.person.name").value("John Doe")
        )
    }

    @Test
    fun `should create account even if personId is wrong`(){
        // GIVEN
        val person = Person(
            firstname = "John",
            lastname = "Doe",
            federation = "USA",
            birthday = null,
            gender = Gender.MALE,
            accounts = emptySet()
        )
        val savedPerson = personRepository.save(person)
        var randomUUID: UUID?
        do {
            randomUUID = UUID.randomUUID()
        } while (savedPerson.id == randomUUID)

        val accountDto = WriteAccountDto(
            platformId = "12345",
            name = "Test Account",
            username = "testuser",
            platform = ChessPlatform.CHESSCOM,
            personId = randomUUID.toString()
        )

        // WHEN
        mockMvc.perform(
            post("/api/accounts")
                .with(user("user").authorities(SimpleGrantedAuthority("CHESS_SERVICE")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDto))
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isCreated,
            jsonPath("$.id").exists(),
            jsonPath("$.person.id").doesNotExist(),
            jsonPath("$.person.name").doesNotExist()
        )
    }
}
