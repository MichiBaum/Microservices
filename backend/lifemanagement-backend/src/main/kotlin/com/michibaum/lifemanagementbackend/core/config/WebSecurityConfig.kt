package com.michibaum.lifemanagementbackend.core.config

import com.michibaum.lifemanagementbackend.core.publicendpoint.PublicEndpointDetails
import com.michibaum.lifemanagementbackend.core.security.*
import com.michibaum.lifemanagementbackend.user.repository.JWTRepository
import org.h2.server.web.WebServlet
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
class WebSecurityConfig(

    private val userDetailsService: UserDetailsServiceImpl,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val lastLoginUpdater: LastLoginUpdater,
    private val publicEndpoints: List<PublicEndpointDetails>,
    private val loginLogCreator: LoginLogCreator,
    private val startingSecret: String,
    private val jwtFactory: JWTFactory,
    private val jwtRepository: JWTRepository

) : WebSecurityConfigurerAdapter() {

    @Value("\${application.system.environment}")
    private val systemEnvironment: String = ""

    @Value("\${swagger.enabled}")
    private val swaggerEnabled: Boolean = false

    @Value("\${application.version}")
    private val applicationVersion: String = ""

    override fun configure(http: HttpSecurity) {
        var antEndpoints: Array<String> = publicEndpoints.map(PublicEndpointDetails::antPaths).flatten().toTypedArray()

//      Only if the profile is dev_h2
        if (systemEnvironment == "dev_h2") {
            antEndpoints = antEndpoints.plus("/lifemanagement/api/h2-console/**")
        }

        if(swaggerEnabled){
            antEndpoints = antEndpoints.plus("/lifemanagement/api/swagger-ui.html")
            antEndpoints = antEndpoints.plus("/lifemanagement/api/swagger-ui/**")
            antEndpoints = antEndpoints.plus("/lifemanagement/api/api-docs/**")
        }

        http.cors().and().authorizeRequests()
            .antMatchers(*antEndpoints).permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(
                JWTAuthenticationFilter(
                    authenticationManager(),
                    lastLoginUpdater,
                    loginLogCreator,
                    jwtFactory
                )
            )
            .addFilter(
                JWTAuthorizationFilter(
                    authenticationManager(),
                    userDetailsService,
                    lastLoginUpdater,
                    applicationVersion,
                    startingSecret,
                    jwtRepository
                )
            )
            // this disables session creation on Spring Security
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        http.csrf().disable()
        http.headers().frameOptions().disable()
    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(bCryptPasswordEncoder)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? =
        UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration(
                "/**",
                CorsConfiguration().applyPermitDefaultValues()
            )
        }

//  Only if the profile is dev_h2
    @Profile("dev_h2")
    @Bean
    fun h2servletRegistration(): ServletRegistrationBean<WebServlet> =
        ServletRegistrationBean(WebServlet()).apply { addUrlMappings("/h2-console/*") }

}
