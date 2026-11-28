package com.michibaum.authentication_service.app.basic_authentication

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.databind.ObjectMapper
import com.michibaum.authentication_library.AuthenticationClient
import com.michibaum.authentication_library.PublicKeyDto
import com.michibaum.authentication_library.public_endpoints.PublicEndpointResolver
import com.michibaum.authentication_library.security.ServletAuthenticationFilter
import com.michibaum.authentication_library.security.ServletDelegateAuthenticationManager
import com.michibaum.authentication_library.security.jwt.JwsValidator
import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.authentication_library.security.jwt.JwtAuthenticationConverter
import com.michibaum.authentication_library.security.jwt.JwtAuthenticationManager
import com.michibaum.authentication_service.config.SecurityConfiguration
import com.michibaum.permission_library.Permissions
import io.micrometer.observation.ObservationRegistry
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

@WebMvcTest(controllers = [ChangePasswordController::class])
@Import(SecurityConfiguration::class, BasicAuthenticationControllerAdvice::class, ChangePasswordControllerTest.TestConfig::class)
class ChangePasswordControllerTest {

    companion object {
        private val keyPair: KeyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        private val foreignKeyPair: KeyPair = KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
    }

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var jwsValidator: JwsValidator

    @MockitoBean
    lateinit var changePasswordService: ChangePasswordService

    private val userId = "11111111-2222-3333-4444-555555555555"

    @TestConfiguration
    class TestConfig {
        @Bean
        fun publicEndpointResolver(): PublicEndpointResolver {
            val resolver = org.mockito.Mockito.mock(PublicEndpointResolver::class.java)
            org.mockito.Mockito.`when`(resolver.run()).thenReturn(emptyList())
            return resolver
        }

        @Bean
        fun jwsValidator(): JwsValidator {
            val authenticationClient = mockk<AuthenticationClient>()
            every { authenticationClient.publicKey() } returns PublicKeyDto("RSA", keyPair.public.encoded)
            return JwsValidator(authenticationClient).apply { reloadPublicKey() }
        }

        @Bean
        fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
            return JwtAuthenticationConverter()
        }

        @Bean
        fun authenticationFilter(
            jwtAuthenticationConverter: JwtAuthenticationConverter,
            jwsValidator: JwsValidator
        ): ServletAuthenticationFilter {
            val authenticationManager = ServletDelegateAuthenticationManager(
                listOf(JwtAuthenticationManager(jwsValidator, ObservationRegistry.create()))
            )
            return ServletAuthenticationFilter(
                authenticationManager = authenticationManager,
                converters = listOf(jwtAuthenticationConverter)
            )
        }
    }

    private fun signedToken(userId: String? = this.userId, signingKeyPair: KeyPair = keyPair): String {
        val builder = JWT.create()
            .withIssuer("authentication-service")
            .withSubject("testuser")
            .withArrayClaim("permissions", arrayOf(Permissions.AUTHENTICATION_SERVICE.name))
        if (userId != null) {
            builder.withClaim("userId", userId)
        }
        return builder.sign(
            Algorithm.RSA256(signingKeyPair.public as RSAPublicKey, signingKeyPair.private as RSAPrivateKey)
        )
    }

    private fun createJwtAuthentication(userId: String = this.userId): JwtAuthentication {
        val userDetails = org.springframework.security.core.userdetails.User.builder()
            .username("testuser")
            .password("")
            .authorities(emptyList())
            .build()
        val token = JWT.create()
            .withSubject("testuser")
            .withClaim("userId", userId)
            .sign(Algorithm.none())
        val jwtAuth: JwtAuthentication = mockk()
        every { jwtAuth.token } returns token
        every { jwtAuth.isAuthenticated } returns true
        every { jwtAuth.authorities } returns emptyList()
        every { jwtAuth.name } returns "testuser"
        every { jwtAuth.principal } returns userDetails
        every { jwtAuth.credentials } returns ""
        return jwtAuth
    }

    private fun createRealJwtAuthentication(userId: String? = this.userId): JwtAuthentication {
        val userDetails = org.springframework.security.core.userdetails.User.builder()
            .username("testuser")
            .password("")
            .authorities(emptyList())
            .build()
        val builder = JWT.create().withSubject("testuser")
        if (userId != null) {
            builder.withClaim("userId", userId)
        }
        val token = builder.sign(Algorithm.none())
        val jwtAuth = JwtAuthentication(token, userDetails)
        jwtAuth.isAuthenticated = true
        return jwtAuth
    }

    @Test
    fun `changePassword returns 401 when request is unauthenticated at security filter boundary`() {
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isUnauthorized)

        org.mockito.Mockito.verifyNoInteractions(changePasswordService)
    }

    @Test
    fun `changePassword returns 401 when request carries malformed Bearer token in filter chain`() {
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer not.a.valid.jwt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isUnauthorized)

        org.mockito.Mockito.verifyNoInteractions(changePasswordService)
    }

    @Test
    fun `changePassword returns 401 when signed Bearer token is missing userId in filter chain`() {
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${signedToken(userId = null)}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isUnauthorized)

        org.mockito.Mockito.verifyNoInteractions(changePasswordService)
    }

    @Test
    fun `changePassword returns 401 when Bearer token signature does not verify in filter chain`() {
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${signedToken(signingKeyPair = foreignKeyPair)}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isUnauthorized)

        org.mockito.Mockito.verifyNoInteractions(changePasswordService)
    }

    @Test
    fun `changePassword returns 204 when validly signed Bearer token carries userId in filter chain`() {
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${signedToken()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isNoContent)

        org.mockito.Mockito.verify(changePasswordService).changePassword(userId, payload)
    }

    @Test
    fun `changePassword returns 204 on successful password change with valid JwtAuthentication`() {
        val jwtAuth = createJwtAuthentication()
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(jwtAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isNoContent)

        org.mockito.Mockito.verify(changePasswordService).changePassword(userId, payload)
    }

    @Test
    fun `changePassword returns 204 on successful password change with real JwtAuthentication`() {
        val realJwtAuth = createRealJwtAuthentication(userId)
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(realJwtAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isNoContent)

        org.mockito.Mockito.verify(changePasswordService).changePassword(userId, payload)
    }

    @Test
    fun `changePassword returns 401 when real JwtAuthentication is missing userId claim`() {
        val jwtAuthWithoutUserId = createRealJwtAuthentication(userId = null)
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(jwtAuthWithoutUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isUnauthorized)

        org.mockito.Mockito.verifyNoInteractions(changePasswordService)
    }

    @Test
    fun `changePassword returns 401 when authentication is not JwtAuthentication`() {
        val nonJwtAuth = UsernamePasswordAuthenticationToken("username", "password", emptyList())
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(nonJwtAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isUnauthorized)

        org.mockito.Mockito.verifyNoInteractions(changePasswordService)
    }

    @Test
    fun `changePassword returns 401 when JwtAuthentication has blank userId`() {
        val blankJwtAuth = createJwtAuthentication(userId = "")
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(blankJwtAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpect(status().isUnauthorized)

        org.mockito.Mockito.verifyNoInteractions(changePasswordService)
    }

    @Test
    fun `changePassword returns 400 when currentPassword is blank`() {
        val jwtAuth = createJwtAuthentication()
        val payload = ChangePasswordDto(
            currentPassword = "   ",
            newPassword = "NewSecurePassword456!"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(jwtAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpectAll(
                status().isBadRequest,
                jsonPath("$.status").value(400),
                jsonPath("$.code").value("VALIDATION_FAILED"),
                jsonPath("$.title").value("VALIDATION_FAILED"),
                jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("currentPassword"))
            )

        org.mockito.Mockito.verifyNoInteractions(changePasswordService)
    }

    @Test
    fun `changePassword returns 400 when newPassword is less than 8 characters`() {
        val jwtAuth = createJwtAuthentication()
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "short"
        )

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(jwtAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpectAll(
                status().isBadRequest,
                jsonPath("$.status").value(400),
                jsonPath("$.code").value("VALIDATION_FAILED"),
                jsonPath("$.title").value("VALIDATION_FAILED"),
                jsonPath("$.detail").value(org.hamcrest.Matchers.containsString("newPassword"))
            )

        org.mockito.Mockito.verifyNoInteractions(changePasswordService)
    }

    @Test
    fun `changePassword returns 400 problem detail when service throws InvalidCurrentPasswordException`() {
        val jwtAuth = createJwtAuthentication()
        val payload = ChangePasswordDto(
            currentPassword = "WrongPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        org.mockito.Mockito.doThrow(InvalidCurrentPasswordException("Current password does not match"))
            .`when`(changePasswordService).changePassword(userId, payload)

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(jwtAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpectAll(
                status().isBadRequest,
                jsonPath("$.status").value(400),
                jsonPath("$.code").value("INVALID_CURRENT_PASSWORD"),
                jsonPath("$.title").value("INVALID_CURRENT_PASSWORD"),
                jsonPath("$.detail").value("Current password does not match")
            )
    }

    @Test
    fun `changePassword returns 400 problem detail when service throws SamePasswordException`() {
        val jwtAuth = createJwtAuthentication()
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "OldPassword123!"
        )

        org.mockito.Mockito.doThrow(SamePasswordException("New password must be different from current password"))
            .`when`(changePasswordService).changePassword(userId, payload)

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(jwtAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpectAll(
                status().isBadRequest,
                jsonPath("$.status").value(400),
                jsonPath("$.code").value("SAME_PASSWORD"),
                jsonPath("$.title").value("SAME_PASSWORD"),
                jsonPath("$.detail").value("New password must be different from current password")
            )
    }

    @Test
    fun `changePassword returns 400 problem detail when service throws UserCredentialsNotFoundException`() {
        val jwtAuth = createJwtAuthentication()
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        org.mockito.Mockito.doThrow(UserCredentialsNotFoundException("User credentials not found"))
            .`when`(changePasswordService).changePassword(userId, payload)

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(jwtAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpectAll(
                status().isBadRequest,
                jsonPath("$.status").value(400),
                jsonPath("$.code").value("NO_PASSWORD_CREDENTIALS"),
                jsonPath("$.title").value("NO_PASSWORD_CREDENTIALS"),
                jsonPath("$.detail").value("User credentials not found")
            )
    }

    @Test
    fun `changePassword returns 403 problem detail when service throws AccountDisabledException`() {
        val jwtAuth = createJwtAuthentication()
        val payload = ChangePasswordDto(
            currentPassword = "OldPassword123!",
            newPassword = "NewSecurePassword456!"
        )

        org.mockito.Mockito.doThrow(AccountDisabledException("Account is disabled"))
            .`when`(changePasswordService).changePassword(userId, payload)

        mockMvc.perform(
            post("/api/basic-authentication/change-password")
                .with(authentication(jwtAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))
        )
            .andExpectAll(
                status().isForbidden,
                jsonPath("$.status").value(403),
                jsonPath("$.code").value("ACCOUNT_DISABLED"),
                jsonPath("$.title").value("ACCOUNT_DISABLED"),
                jsonPath("$.detail").value("Account is disabled")
            )
    }
}
