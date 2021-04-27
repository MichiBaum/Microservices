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
                        .id("admin-service")
                )

                // Registry
                .route(r -> r.path("/services/registry/**") //TODO doesnt work
                        .uri("lb://registry")
                        .id("registry-service")
                )

                // Actuator Kibana
                .route(r -> r.path("/kibana/actuator/**")
                        .uri("http://actuator-kibana:5601")
                        .id("actuator-kibana")
                )

                // Zipkin Kibana
                .route(r -> r.path("/kibana/zipkin/**")
                        .uri("http://zipkin-kibana:5601")
                        .id("zipkin-kibana")
                )

                // Zipkin
                .route(r -> r.path("/zipkin/**")
                        .uri("http://zipkin:9411")
                        .id("zipkin")
                )

                .build();
    }

}
