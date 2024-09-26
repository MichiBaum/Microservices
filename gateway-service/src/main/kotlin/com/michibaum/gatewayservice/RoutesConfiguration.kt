package com.michibaum.gatewayservice

import com.michibaum.permission_library.Permissions
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RoutesConfiguration(
//    private val authenticationFilter: GatewayFilter
) {

    companion object {
        const val ADMIN_SERVICE_HOST = "admin.michibaum.ch"
        const val ADMIN_SERVICE_URI = "lb://admin-service"
        const val AUTHENTICATION_SERVICE_HOST = "authentication.michibaum.ch"
        const val AUTHENTICATION_SERVICE_URI = "lb://authentication-service"
        const val JAVADOC_SERVICE_HOST = "javadoc.michibaum.ch"
        const val JAVADOC_SERVICE_URI = "lb://javadoc-service"
        const val REGISTRY_SERVICE_HOST = "registry.michibaum.ch"
        const val REGISTRY_SERVICE_URI = "lb://registry-service"
        const val USERMANAGEMENT_SERVICE_HOST = "usermanagement.michibaum.ch"
        const val USERMANAGEMENT_SERVICE_URI = "lb://usermanagement-service"
        const val WEBSITE_SERVICE_HOST = "michibaum.ch"
        const val WEBSITE_SERVICE_URI = "lb://website-service"
    }

    /**
     * Creates and returns a RouteLocator bean that defines the routes for the application.
     *
     * @param builder The RouteLocatorBuilder used to build the routes.
     * @return A RouteLocator object that represents the defined routes.
     */
    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes {
            route {
                host(ADMIN_SERVICE_HOST)
                filters {
//                    authenticationFilter
                    AuthorizationPreFilter(Permissions.AdminService.CAN_SEND_REQUEST)
                }
                uri(ADMIN_SERVICE_URI)
            }
            route {
                host(AUTHENTICATION_SERVICE_HOST)
                filters {

                }
                uri(AUTHENTICATION_SERVICE_URI)
            }
            route {
                host(JAVADOC_SERVICE_HOST)
                filters {
//                    authenticationFilter
                    AuthorizationPreFilter(Permissions.JavaDocService.CAN_READ)
                }
                uri(JAVADOC_SERVICE_URI)
            }
            route {
                host(REGISTRY_SERVICE_HOST)
                filters {
//                    authenticationFilter
                    AuthorizationPreFilter()
                }
                uri(REGISTRY_SERVICE_URI)
            }
            route {
                host(USERMANAGEMENT_SERVICE_HOST)
                filters {
//                    authenticationFilter
                    AuthorizationPreFilter()
                }
                uri(USERMANAGEMENT_SERVICE_URI)
            }
            route {
                host(WEBSITE_SERVICE_HOST)
                filters {

                }
                uri(WEBSITE_SERVICE_URI)
            }
        }
    }

}