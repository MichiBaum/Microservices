package com.michibaum.authentication_service.app.basic_authentication

import com.auth0.jwt.JWT
import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import io.micrometer.observation.annotation.Observed
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ChangePasswordController(
    private val changePasswordService: ChangePasswordService
) {

    @Observed(name = "basic-authentication.change-password")
    @PostMapping("/api/basic-authentication/change-password")
    fun changePassword(
        @Valid @RequestBody dto: ChangePasswordDto,
        authentication: Authentication?
    ): ResponseEntity<Void> {
        val userId = if (authentication is JwtAuthentication) {
            JWT.decode(authentication.token).getClaim("userId").asString()?.takeIf { it.isNotBlank() }
        } else {
            null
        }

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        changePasswordService.changePassword(userId, dto)
        return ResponseEntity.noContent().build()
    }
}
