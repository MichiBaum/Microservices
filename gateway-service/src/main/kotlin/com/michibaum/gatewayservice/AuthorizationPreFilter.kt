package com.michibaum.gatewayservice

import com.michibaum.permission_library.PermissionUtil
import com.michibaum.permission_library.Permissions
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class AuthorizationPreFilter() : GatewayFilter {

    private var permissions: List<PermissionUtil> = listOf()

    constructor(vararg permissions: PermissionUtil) : this() {
        this.permissions = permissions.asList()
    }

    override fun filter(exchange: ServerWebExchange?, chain: GatewayFilterChain?): Mono<Void> {
        exchange?.let {
            // permissions.get(0).toPermissionString()
        }
        chain?.let {

        }
        return Mono.empty()
    }

}