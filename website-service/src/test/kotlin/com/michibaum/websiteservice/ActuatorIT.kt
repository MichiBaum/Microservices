package com.michibaum.websiteservice

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest(properties = [
    "spring.boot.admin.client.username=someUsername",
    "spring.boot.admin.client.password=somePasswööörd"
])
class ActuatorIT {

    @Autowired
    lateinit var webClient: MockMvc

    @Test
    fun `actuator without authentication returns 401`(){
        // GIVEN

        // WHEN
        webClient.perform(get("/actuator"))
            .andExpect(status().isUnauthorized())

        // THEN

    }

    @Test
    fun `actuator health without authentication returns 401`(){
        // GIVEN

        // WHEN
        webClient.perform(get("/actuator/health"))
            .andExpect(status().isUnauthorized())

        // THEN

    }

    @Test
    fun `actuator info without authentication returns 401`(){
        // GIVEN

        // WHEN
        webClient.perform(get("/actuator/info"))
            .andExpect(status().isUnauthorized())

        // THEN

    }

    @Test
    fun `actuator with authentication returns 200`(){
        // GIVEN
        val basicAuth = "someUsername:somePasswööörd"
        val basicAuthEncoded = Base64.getEncoder().encodeToString(basicAuth.toByteArray())

        // WHEN
        webClient.perform(get("/actuator").header("Authorization", "Basic $basicAuthEncoded"))
            .andExpect(status().isOk()) // TODO returns 302 redirect to / because of success authentication

        // THEN

    }

    @Test
    fun `actuator health with authentication returns 200`(){
        // GIVEN
        val basicAuth = "someUsername:somePasswööörd"
        val basicAuthEncoded = Base64.getEncoder().encodeToString(basicAuth.toByteArray())

        // WHEN
        webClient.perform(get("/actuator/health").header("Authorization", "Basic $basicAuthEncoded"))
            .andExpect(status().isOk()) // TODO returns 302 redirect to / because of success authentication

        // THEN

    }

    @Test
    fun `actuator info with authentication returns 200`(){
        // GIVEN
        val basicAuth = "someUsername:somePasswööörd"
        val basicAuthEncoded = Base64.getEncoder().encodeToString(basicAuth.toByteArray())

        // WHEN
        webClient.perform(get("/actuator/info").header("Authorization", "Basic $basicAuthEncoded"))
            .andExpect(status().isOk()) // TODO returns 302 redirect to / because of success authentication

        // THEN

    }

}