package com.michibaum.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ServiceUrlFilter implements GatewayFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        exchange.addUrlTransformer(s -> s.replaceFirst("/services/[a-z]+", ""));
        chain.filter(exchange);
        return null;
    }

}
