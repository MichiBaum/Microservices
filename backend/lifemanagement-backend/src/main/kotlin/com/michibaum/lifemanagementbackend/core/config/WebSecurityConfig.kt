package com.michibaum.lifemanagementbackend.core.config

import com.michibaum.lifemanagementbackend.core.publicendpoint.PublicEndpointDetails
import com.michibaum.lifemanagementbackend.security.JWTAuthenticationFilter
import com.michibaum.lifemanagementbackend.security.JWTAuthorizationFilter
import com.michibaum.lifemanagementbackend.security.LastLoginUpdater
import com.michibaum.lifemanagementbackend.security.UserDetailsServiceImpl
import com.michibaum.lifemanagementbackend.user.repository.UserRepository
import mu.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
    private val userRepository: UserRepository,
    private val lastLoginUpdater: LastLoginUpdater,
    private val publicEndpoints: List<PublicEndpointDetails>

): WebSecurityConfigurerAdapter() {

    private val logger = KotlinLogging.logger {}

    override fun configure(http: HttpSecurity) {
        val antEndpoints: Array<String> = publicEndpoints.map(PublicEndpointDetails::antPaths).flatten().toTypedArray()
        antEndpoints.forEach{ antEndpoint: String -> logger.info("Public AntEndpoint '$antEndpoint' found") }

        http.cors().and().authorizeRequests()
            .antMatchers(*antEndpoints).permitAll()
            .antMatchers("/console/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilter(
                JWTAuthenticationFilter(
                    authenticationManager(),
                    userRepository
                )
            )
            .addFilter(
                JWTAuthorizationFilter(
                    authenticationManager(),
                    userDetailsService,
                    lastLoginUpdater
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
        UrlBasedCorsConfigurationSource().apply { registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues()) }

}
