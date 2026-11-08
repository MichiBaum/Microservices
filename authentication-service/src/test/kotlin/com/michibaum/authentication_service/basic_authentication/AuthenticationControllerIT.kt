package com.michibaum.authentication_service.basic_authentication

import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.authentication_service.TestcontainersConfiguration
import com.michibaum.authentication_service.app.basic_authentication.AuthenticationDto
import com.michibaum.authentication_service.database.UserCredentials
import com.michibaum.authentication_service.database.UserCredentialsRepository
import com.michibaum.usermanagement_library.UserDetailsDto
import com.michibaum.usermanagement_library.UsermanagementClient
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@TestcontainersConfiguration
class AuthenticationControllerIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var userCredentialsRepository: UserCredentialsRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var usermanagementClient: UsermanagementClient

    @Test
    fun `getAuthDetails endpoint returns result`() {
        mockMvc.perform(get("/api/getAuthDetails"))
            .andExpectAll(
                status().isOk,
                jsonPath("$.algorithm").isNotEmpty,
                jsonPath("$.key").isNotEmpty
            )
    }

    @Test
    fun `authenticate endpoint returns token for valid credentials`() {
        val username = "auth_it_user"
        val password = "StrongPassword123"
        val userId = "11111111-2222-3333-4444-555555555555"

        val credentials = UserCredentials(
            userId = userId,
            username = username,
            passwordHash = passwordEncoder.encode(password)!!,
            enabled = true
        )
        userCredentialsRepository.save(credentials)

        `when`(usermanagementClient.getUserDetailsById(userId)).thenReturn(
            UserDetailsDto(id = userId, permissions = setOf("CHESS_SERVICE"))
        )

        val requestPayload = AuthenticationDto(username = username, password = password)

        mockMvc.perform(
            post("/api/basic-authentication/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload))
        )
            .andExpectAll(
                status().isOk,
                jsonPath("$.username").value(username),
                jsonPath("$.jwt").isNotEmpty
            )
    }

    @Test
    fun `authenticate endpoint returns 401 for unknown user`() {
        val requestPayload = AuthenticationDto(username = "unknown_user_999", password = "SomePassword")

        mockMvc.perform(
            post("/api/basic-authentication/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `authenticate endpoint returns 400 for blank credentials`() {
        val requestPayload = AuthenticationDto(username = "   ", password = "")

        mockMvc.perform(
            post("/api/basic-authentication/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestPayload))
        )
            .andExpect(status().isBadRequest)
    }
}