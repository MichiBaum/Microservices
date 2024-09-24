package com.michibaum.gatewayservice

import org.slf4j.LoggerFactory
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

    private val logger = LoggerFactory.getLogger(AuthenticationFilter::class.java)

    /**
     * Filters the incoming request based on the presence and validity of the Authorization header.
     *
     * @param exchange The ServerWebExchange object representing the current HTTP exchange.
     * @param chain The GatewayFilterChain object representing the chain of filters to be executed.
     * @return A Mono<Void> that represents the completion of the filter chain.
     * @throws Exception if the ServerWebExchange or GatewayFilterChain is null.
     */
    override fun filter(
        exchange: ServerWebExchange?,
        chain: GatewayFilterChain?
    ): Mono<Void> {
        exchange?.let {
            logger.info(requestLog(it))
            val authHeaders = it.request.headers["Authorization"]
            val headerExists = authHeaders?.size == 1

            return if (headerExists) {
                val authHeader = authHeaders?.get(0)
                if (authenticationValidator.valid(authHeader ?: "")) {
                    logger.info(requestLog(it))
                    chain?.filter(exchange) ?: Mono.empty() // Continue the filter chain if it isn't null. If it is null, return an empty Mono.
                } else {
                    logger.info(requestLog(exchange))
                    handleAuthenticationFailure(it)
                }
            } else {
                logger.info(requestLog(exchange))
                handleAuthenticationFailure(it)
            }
        }

        logger.info(requestLog(exchange))
        return Mono.error(Exception("ServerWebExchange or GatewayFilterChain is null"))
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