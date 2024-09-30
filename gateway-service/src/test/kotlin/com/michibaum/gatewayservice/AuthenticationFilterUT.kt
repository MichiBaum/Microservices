package com.michibaum.gatewayservice

import com.michibaum.authentication_library.AuthenticationClient
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@ExtendWith(MockitoExtension::class)
class AuthenticationFilterUT{

    private val jwsValidator: JWSValidator = mockk()

    @Test
    fun `exchange null returns error`(){
        // GIVEN
        val authenticationFilter = AuthenticationFilter(jwsValidator)

        val gatewayFilterChain: GatewayFilterChain = mockk()

        // WHEN
        val result = authenticationFilter.filter(null, gatewayFilterChain)

        //THEN
        assertThrows(
            Exception::class.java,
            result::block,
            "ServerWebExchange is null"
        )
    }

    @Test
    fun `authorization header does not exist`(){
        // GIVEN
        val authenticationFilter = AuthenticationFilter(jwsValidator)

        val gatewayFilterChain: GatewayFilterChain = mockk()
        val httpResponse: ServerHttpResponse = mockk {
            every { setStatusCode(any()) } returns true
            every { setComplete() } returns Mono.empty()
        }
        val serverWebExchange: ServerWebExchange = mockk{
            every { response } returns httpResponse
            every { request } returns mockk {
                every { headers } returns mockk {
                    every { headers["Authorization"] } returns null
                }
            }
        }

        // WHEN
        authenticationFilter.filter(serverWebExchange, gatewayFilterChain)

        //THEN
        verify (exactly = 1) { httpResponse.setStatusCode(HttpStatus.FORBIDDEN) }

    }

}