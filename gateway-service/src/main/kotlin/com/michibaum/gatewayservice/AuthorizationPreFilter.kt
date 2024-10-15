package com.michibaum.gatewayservice

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class AuthorizationPreFilter() : GatewayFilter {

    private val logger = LoggerFactory.getLogger(AuthorizationPreFilter::class.java)
    private lateinit var permissions: List<String>

    constructor(vararg permissions: String) : this() {
        this.permissions = permissions.asList()
    }

    override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {
        exchange?.let {
             permissions[0]
        }
        chain?.let {

        }
        return Mono.empty()
    }

}