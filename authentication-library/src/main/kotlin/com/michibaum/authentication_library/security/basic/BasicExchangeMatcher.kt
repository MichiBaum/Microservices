package com.michibaum.authentication_library.security.basic

import org.springframework.http.HttpHeaders
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class BasicExchangeMatcher: ServerWebExchangeMatcher {
    override fun matches(exchange: ServerWebExchange?): Mono<ServerWebExchangeMatcher.MatchResult> {
        val authHeader = exchange?.request?.headers?.getFirst(HttpHeaders.AUTHORIZATION)
        if(authHeader != null && authHeader.startsWith("Basic ")) {
            return ServerWebExchangeMatcher.MatchResult.match()
        }
        return ServerWebExchangeMatcher.MatchResult.notMatch()
    }
}