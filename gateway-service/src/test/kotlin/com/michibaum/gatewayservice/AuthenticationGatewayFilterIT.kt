package com.michibaum.gatewayservice

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.michibaum.authentication_library.security.jwt.JwsValidator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(properties = [
    "spring.cloud.gateway.routes[0].id=test",
    "spring.cloud.gateway.routes[0].uri=http://localhost:8099",
    "spring.cloud.gateway.routes[0].filters[0].name=Authentication",
    "spring.cloud.gateway.routes[0].predicates[0]=Path=/post"
])
@AutoConfigureWebTestClient
class AuthenticationGatewayFilterIT{

    companion object {
        @RegisterExtension
        var wireMockServer: WireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().port(8099))
            .build()
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @AfterEach
    fun afterEach() {
        wireMockServer.resetAll()
    }

    @MockBean
    lateinit var jwsValidator: JwsValidator

    @Test
    fun `without authentication header gets forbidden`(){
        // GIVEN
        wireMockServer.stubFor(post("/post").willReturn(ok()))

        // WHEN
        webTestClient.post().uri("/post")
            .exchange()
            .expectStatus().isForbidden

        // THEN
        wireMockServer.verify(0, postRequestedFor(urlEqualTo("/post")))

    }

    @Test
    fun `with valid authentication header forwards request`(){
        // GIVEN
        wireMockServer.stubFor(post("/post").willReturn(ok()))
        `when`(jwsValidator.validate(anyString())).thenReturn(true)

        // WHEN
        webTestClient.post().uri("/post")
            .headers { headers ->
                headers.setBearerAuth("1234567890")
            }
            .exchange()
            .expectStatus().isOk

        // THEN
        wireMockServer.verify(1, postRequestedFor(urlEqualTo("/post")))

    }

    @Test
    fun `with invalid authentication header gets forbidden`(){
        // GIVEN
        wireMockServer.stubFor(post("/post").willReturn(ok()))
        `when`(jwsValidator.validate(anyString())).thenReturn(false)

        // WHEN
        webTestClient.post().uri("/post")
            .headers { headers ->
                headers.setBearerAuth("1234567890")
            }
            .exchange()
            .expectStatus().isForbidden

        // THEN
        wireMockServer.verify(0, postRequestedFor(urlEqualTo("/post")))

    }

}