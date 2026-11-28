package com.michibaum.gatewayservice.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse

@Configuration
@EnableConfigurationProperties(value = [GatewayStaticRoutesProperties::class])
class RouterConfiguration {

    @Bean
    fun gatewayRoutes(dynamicGatewayRouter: DynamicGatewayRouter): RouterFunction<ServerResponse> {
        return RouterFunction { request ->
            dynamicGatewayRouter.currentRouter().route(request)
        }
    }

}