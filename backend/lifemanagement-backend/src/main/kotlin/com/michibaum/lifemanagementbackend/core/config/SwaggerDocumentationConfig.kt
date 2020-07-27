package com.michibaum.lifemanagementbackend.core.config

import com.michibaum.lifemanagementbackend.core.security.SecurityConstants
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.SwaggerUiConfigProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerDocumentationConfig {

    @Bean
    fun customOpenApi(
        @Value("\${application.description}") applicationDesciption: String = "",
        @Value("\${application.version}") applicationVersion: String = ""
    ): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                .description(applicationDesciption)
                .title("Lifemanagement Api")
                .version(applicationVersion)
                .termsOfService("http://swagger.io/terms/")
                .contact(
                    Contact()
                        .name("Michael Baumberger")
                        .url("michibaum.ch/lifemanagement/")
                        .email("michael_baumberger@gmx.ch")
                )
            ).schemaRequirement(
                "Barear Token",
                SecurityScheme()
                    .type(SecurityScheme.Type.APIKEY)
                    .name(SecurityConstants.HEADER_STRING)
                    .`in`(SecurityScheme.In.HEADER)
                    .flows(
                        OAuthFlows()
                            .clientCredentials(
                                OAuthFlow()
                                .authorizationUrl("michibaum.ch/lifemanagement/")
                            )
                    )
            )

    }

}
