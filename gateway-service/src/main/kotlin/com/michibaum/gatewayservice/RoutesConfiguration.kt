package com.michibaum.gatewayservice

import com.michibaum.permission_library.Permissions
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RoutesConfiguration {

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes {
            route {
                host("admin.michibaum.ch")
//                filters { AuthorizationPreFilter(Permissions.Admin_Service.CAN_SEND_REQUEST) }
                uri("lb://admin-service")
            }
            route {
                host("authentication.michibaum.ch")
//                filters {}
                uri("lb://authentication-service")
            }
            route {
                host("javadoc.michibaum.ch")
//                filters { AuthorizationPreFilter(Permissions.JavaDoc_Service.CAN_READ) }
                uri("lb://javadoc-service")
            }
            route {
                host("registry.michibaum.ch")
//                filters { AuthorizationPreFilter() }
                uri("lb://registry-service")
            }
            route {
                host("usermanagement.michibaum.ch")
//                filters { AuthorizationPreFilter() }
                uri("lb://usermanagement-service")
            }
            route {
                host("google.michibaum.ch")
//                filters { AuthorizationPreFilter() }
                uri("https://www.google.ch/")
            }
        }
    }

}