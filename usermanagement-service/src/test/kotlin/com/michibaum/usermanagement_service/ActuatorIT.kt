package com.michibaum.usermanagement_service

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.*

@AutoConfigureMockMvc
@SpringBootTest(properties = [
    "spring.boot.admin.service.username=someUsername",
    "spring.boot.admin.service.password=somePasswööörd"
])
@TestcontainersConfiguration
class ActuatorIT {

    @Autowired
    lateinit var mockMvc: MockMvc

    @ParameterizedTest
    @ValueSource(strings = ["/actuator", "/actuator/health", "/actuator/info"])
    fun `actuator endpoints return 401`(endpoint: String){
        // GIVEN

        // WHEN
        mockMvc.perform(get(endpoint))
            .andExpect(status().isUnauthorized)

        // THEN

    }

    @ParameterizedTest
    @ValueSource(strings = ["/actuator", "/actuator/health", "/actuator/info"])
    fun `actuator endpoints with basic authentication return 200`(endpoint: String){
        // GIVEN
        val basicAuth = "someUsername:somePasswööörd"
        val basicAuthEncoded = Base64.getEncoder().encodeToString(basicAuth.toByteArray())

        // WHEN
        mockMvc.perform(
            get(endpoint)
                .header("Authorization", "Basic $basicAuthEncoded")
        )
            .andExpect(status().isOk)

        // THEN

    }

    @ParameterizedTest
    @ValueSource(strings = ["/actuator", "/actuator/health", "/actuator/info"])
    fun `actuator endpoints with not enough authorities return 403`(endpoint: String){
        // GIVEN

        // WHEN
        mockMvc.perform(
            get(endpoint)
                .with(user("user").authorities(SimpleGrantedAuthority("SOME_OTHER_PERMISSION")))
        )
            .andExpect(status().isForbidden)

        // THEN

    }

}