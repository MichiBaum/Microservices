package com.michibaum.gatewayservice

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthenticationFilter(
    private val authenticationValidator: AuthenticationValidator
) : GatewayFilter {

    override fun filter(
        exchange: ServerWebExchange?,
        chain: GatewayFilterChain?
    ): Mono<Void> {
        exchange?.let {
            val authHeaders = it.request.headers["Authorization"]
            val headerExists = authHeaders?.size == 1

            return if (headerExists) {
                val authHeader = authHeaders?.get(0)
                if (authenticationValidator.valid(authHeader ?: "")) {
                    chain?.filter(exchange) ?: Mono.empty() // Continue the filter chain if it isn't null. If it is null, return an empty Mono.
                } else {
                    handleAuthenticationFailure(it)
                }
            } else {
                handleAuthenticationFailure(it)
            }
        }

        return Mono.error(Exception("ServerWebExchange or GatewayFilterChain is null"))
    }

    private fun handleAuthenticationFailure(exchange: ServerWebExchange): Mono<Void> {
        val response: ServerHttpResponse = exchange.response
        response.setStatusCode(HttpStatus.FORBIDDEN)
        return response.setComplete()
    }
}