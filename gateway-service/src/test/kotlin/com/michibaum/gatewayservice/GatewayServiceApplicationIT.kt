package com.michibaum.gatewayservice

import com.michibaum.gateway_discovery.model.CorsMetadata
import com.michibaum.gateway_discovery.model.GatewayServiceMetadata
import com.michibaum.gateway_discovery.model.RouteDefinitionMetadata
import com.michibaum.gatewayservice.config.DynamicGatewayCorsConfigurationSource
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.resttestclient.TestRestTemplate
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@AutoConfigureTestRestTemplate
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayServiceApplicationIT {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    lateinit var corsConfigurationSource: DynamicGatewayCorsConfigurationSource

    @Test
    fun contextLoads() {
        assertNotNull(applicationContext)
    }

    @Test
    fun `enforces host-scoped CORS preflight headers across services`() {
        corsConfigurationSource.rebuild(
            listOf(
                GatewayServiceMetadata(
                    serviceId = "chess-service",
                    routes = listOf(RouteDefinitionMetadata(hosts = listOf("chess.michibaum.*"), order = 0)),
                    cors = CorsMetadata(allowedOriginPatterns = listOf("https://chess-widget.example.com"))
                ),
                GatewayServiceMetadata(
                    serviceId = "vpn-service",
                    routes = listOf(RouteDefinitionMetadata(hosts = listOf("vpn.michibaum.*"), order = 0))
                )
            )
        )

        val chessPreflightHeaders = HttpHeaders().apply {
            add("Host", "chess.michibaum.ch")
            add("Origin", "https://chess-widget.example.com")
            add("Access-Control-Request-Method", "GET")
        }
        val chessResponse = testRestTemplate.exchange(
            "/robots.txt",
            HttpMethod.OPTIONS,
            HttpEntity<Void>(chessPreflightHeaders),
            String::class.java
        )
        assertEquals(HttpStatus.OK, chessResponse.statusCode)
        assertEquals(
            "https://chess-widget.example.com",
            chessResponse.headers.accessControlAllowOrigin
        )

        val vpnPreflightHeaders = HttpHeaders().apply {
            add("Host", "vpn.michibaum.ch")
            add("Origin", "https://chess-widget.example.com")
            add("Access-Control-Request-Method", "GET")
        }
        val vpnResponse = testRestTemplate.exchange(
            "/robots.txt",
            HttpMethod.OPTIONS,
            HttpEntity<Void>(vpnPreflightHeaders),
            String::class.java
        )
        assertEquals(HttpStatus.FORBIDDEN, vpnResponse.statusCode)
        assertNull(vpnResponse.headers.accessControlAllowOrigin)
    }
}