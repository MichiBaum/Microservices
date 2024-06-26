package com.michibaum.gatewayservice

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AuthenticationFilter(private val authenticationValidator: AuthenticationValidator): GatewayFilter {

    override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {
        exchange?.let {
            val authHeaders = it.request.headers["Authorization"]
            val headerExists = (authHeaders?.size ?: 0) == 1
            if(headerExists){
                val authHeader = authHeaders!![0]
                val valid = authenticationValidator.valid(authHeader)
                if(valid) {
                    return chain!!.filter(exchange); // Forward to route
                }
            }
        }
        return exchange!!.let { this.onError(it) }
    }

    private fun onError(exchange: ServerWebExchange): Mono<Void> {
        val response: ServerHttpResponse = exchange.response
        response.setStatusCode(HttpStatus.FORBIDDEN)
        return response.setComplete()
    }
}