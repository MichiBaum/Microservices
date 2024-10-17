package com.michibaum.gatewayservice

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthenticationGatewayFilterFactory(
    private val jwsValidator: JWSValidator
) : AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config>(Config::class.java) {

    data class Config(val message: String? = null)

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
            exchange.let {
                val authHeaders = it.request.headers["Authorization"]
                val headerExists = authHeaders?.size == 1

                return@GatewayFilter if (headerExists) {
                    val authHeader = authHeaders?.get(0)
                    val token = authHeader?.substring("Bearer ".length) ?: ""
                    if (jwsValidator.validate(token)) {
                        chain.filter(exchange) ?: Mono.empty() // Continue the filter chain if it isn't null. If it is null, return an empty Mono.
                    } else {
                        handleAuthenticationFailure(it)
                    }
                } else {
                    handleAuthenticationFailure(it)
                }
            }

        }
    }

    /**
     * Handles authentication failure by setting the response status code to 403 (FORBIDDEN).
     *
     * @param exchange The ServerWebExchange object representing the current HTTP exchange.
     * @return A Mono<Void> that represents the completion of the method.
     */
    private fun handleAuthenticationFailure(exchange: ServerWebExchange): Mono<Void> {
        val response: ServerHttpResponse = exchange.response
        response.setStatusCode(HttpStatus.FORBIDDEN)
        return response.setComplete()
    }

}