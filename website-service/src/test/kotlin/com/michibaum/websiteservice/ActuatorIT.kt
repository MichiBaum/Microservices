package com.michibaum.websiteservice

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@SpringBootTest
class ActuatorIT {

    @Autowired
    lateinit var webClient: MockMvc

    @Test
    fun `actuator is allowed without authentication`(){
        // GIVEN

        // WHEN
        webClient.perform(get("/actuator"))
            .andExpect(status().isOk())

        // THEN

    }

    @Test
    fun `actuator health is allowed without authentication`(){
        // GIVEN

        // WHEN
        webClient.perform(get("/actuator/health"))
            .andExpect(status().isOk())

        // THEN

    }

    @Test
    fun `actuator info is allowed without authentication`(){
        // GIVEN

        // WHEN
        webClient.perform(get("/actuator/info"))
            .andExpect(status().isOk())

        // THEN

    }

}