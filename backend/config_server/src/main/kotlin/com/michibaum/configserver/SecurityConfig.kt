package com.michibaum.configserver

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .csrf()
            .ignoringAntMatchers("**")
            .disable()

        http
            .authorizeRequests()
            .antMatchers("/actuator/**").permitAll()
            .and()
            .httpBasic()
    }
}
