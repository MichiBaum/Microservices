package com.michibaum.authentication_service.authentication

import com.michibaum.authentication_library.PublicKeyDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status


@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerIT {

    @Autowired
    lateinit var client: WebTestClient

    @Test
    fun `getAuthDetails endpoint returns result`() {

        client.get()
            .uri("/getAuthDetails")
            .exchange()
            .expectAll(
                {it.expectStatus().isOk},
                {it.expectBody()
                    .jsonPath("$.algorithm").isNotEmpty
                    .jsonPath("$.key").isNotEmpty
                }
            )
            //.expectBody().consumeWith(System.out::println)

    }

}