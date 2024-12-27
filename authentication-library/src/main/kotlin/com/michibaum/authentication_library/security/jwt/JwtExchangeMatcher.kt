package com.michibaum.authentication_library.security.jwt

import org.springframework.http.HttpHeaders
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class JwtExchangeMatcher: ServerWebExchangeMatcher {
    override fun matches(exchange: ServerWebExchange?): Mono<ServerWebExchangeMatcher.MatchResult> {

        val header = exchange?.request?.headers?.getFirst(HttpHeaders.AUTHORIZATION)

        if(header != null && header.startsWith("Bearer ")) {
            return ServerWebExchangeMatcher.MatchResult.match()
        }

        val cookie = exchange?.request?.cookies?.getFirst("jwt")?.value

        if(!cookie.isNullOrBlank()) {
            return ServerWebExchangeMatcher.MatchResult.match()
        }

        return ServerWebExchangeMatcher.MatchResult.notMatch()

    }
}