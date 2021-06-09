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

                // Admin
                .route(r -> r.path("/services/admin/**")
                        .filters(gatewayFilterSpec ->
                                gatewayFilterSpec
                                        .rewritePath("/services/admin$", "/")
                                        .rewritePath("/services/admin", "")
                        )
                        .uri("lb://admin")
                )

                .build();
    }

}
