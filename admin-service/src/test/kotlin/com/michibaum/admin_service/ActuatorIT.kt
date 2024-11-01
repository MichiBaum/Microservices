package com.michibaum.admin_service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@AutoConfigureMockMvc
@SpringBootTest
class ActuatorIT {

    @Autowired
    lateinit var webClient: WebTestClient

    @Test
    fun `actuator is allowed without authentication`(){
        // GIVEN

        // WHEN
        webClient.get()
            .uri("/actuator")
            .exchange()
            .expectStatus()
            .isOk

        // THEN

    }

    @Test
    fun `actuator health is allowed without authentication`(){
        // GIVEN

        // WHEN
        webClient.get()
            .uri("/actuator/health")
            .exchange()
            .expectStatus()
            .isOk

        // THEN

    }

    @Test
    fun `actuator info is allowed without authentication`(){
        // GIVEN

        // WHEN
        webClient.get()
            .uri("/actuator/info")
            .exchange()
            .expectStatus()
            .isOk

        // THEN

    }

}