package com.michibaum.authentication_service.authentication

import com.michibaum.authentication_service.TestcontainersConfiguration
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
@TestcontainersConfiguration
class AuthenticationControllerIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `getAuthDetails endpoint returns result`() {

        mockMvc.perform(get("/api/getAuthDetails"))
            .andExpectAll(
                status().isOk,
                jsonPath("$.algorithm").isNotEmpty,
                jsonPath("$.key").isNotEmpty
            )

    }

}