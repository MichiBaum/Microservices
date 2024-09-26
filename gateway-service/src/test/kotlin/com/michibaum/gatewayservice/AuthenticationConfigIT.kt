package com.michibaum.gatewayservice

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.MappingBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.PublicKeyDto
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.reactive.server.WebTestClient
import wiremock.org.eclipse.jetty.client.api.Request.RequestListener

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationConfigIT() {

    @Autowired
    lateinit var client: WebTestClient


    @Test
    fun `getAuthDetails should not be found`() {

        client.get()
            .uri("/getAuthDetails")
            .exchange()
            .expectAll(
                {it.expectStatus().isNotFound}
            )

    }

}