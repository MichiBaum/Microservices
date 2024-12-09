package com.michibaum.chess_service

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@AutoConfigureWebTestClient
@SpringBootTest(properties = [
    "spring.boot.admin.client.username=someUsername",
    "spring.boot.admin.client.password=somePasswööörd"
])
class ActuatorIT {

    @Autowired
    lateinit var webClient: WebTestClient

    @Test
    fun `actuator without authentication returns 401`(){
        // GIVEN

        // WHEN
        webClient.get()
            .uri("/actuator")
            .exchange()
            .expectStatus()
            .isUnauthorized

        // THEN

    }

    @Test
    fun `actuator health without authentication returns 401`(){
        // GIVEN

        // WHEN
        webClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus()
            .isUnauthorized

        // THEN

    }

    @Test
    fun `actuator info without authentication returns 401`(){
        // GIVEN

        // WHEN
        webClient.get()
            .uri("/actuator/info")
            .exchange()
            .expectStatus()
            .isUnauthorized

        // THEN

    }

    @Test
    fun `actuator with authentication returns 200`(){
        // GIVEN
        val basicAuth = "someUsername:somePasswööörd"
        val basicAuthEncoded = Base64.getEncoder().encodeToString(basicAuth.toByteArray())

        // WHEN
        webClient.get()
            .uri("/actuator")
            .headers { it.setBasicAuth(basicAuthEncoded) }
            .exchange()
            .expectStatus()
            .isOk

        // THEN

    }

    @Test
    fun `actuator health with authentication returns 200`(){
        // GIVEN
        val basicAuth = "someUsername:somePasswööörd"
        val basicAuthEncoded = Base64.getEncoder().encodeToString(basicAuth.toByteArray())

        // WHEN
        webClient.get()
            .uri("/actuator/health")
            .headers { it.setBasicAuth(basicAuthEncoded) }
            .exchange()
            .expectStatus()
            .isOk

        // THEN

    }

    @Test
    fun `actuator info with authentication returns 200`(){
        // GIVEN
        val basicAuth = "someUsername:somePasswööörd"
        val basicAuthEncoded = Base64.getEncoder().encodeToString(basicAuth.toByteArray())

        // WHEN
        webClient.get()
            .uri("/actuator/info")
            .headers { it.setBasicAuth(basicAuthEncoded) }
            .exchange()
            .expectStatus()
            .isOk

        // THEN

    }

}