package com.michibaum.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/services/admin/**") //TODO doesnt work
                        .uri("lb://admin")
                        .id("admin-service")
                )
                .route(r -> r.path("/services/registry/**") //TODO doesnt work
                        .uri("lb://registry")
                        .id("registry-service")
                )
                .build();
    }

}
