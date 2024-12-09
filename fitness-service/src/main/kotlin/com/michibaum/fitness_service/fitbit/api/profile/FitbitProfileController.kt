package com.michibaum.fitness_service.fitbit.api.profile

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.fitbit.FitbitOAuth
import com.michibaum.fitness_service.fitbit.api.FitbitApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FitbitProfileController(
    private val fitbitApi: FitbitApi,
    private val fitbitOAuth: FitbitOAuth,
    private val fitbitProfileConverter: FitbitProfileConverter,
    private val fitbitProfileService: FitbitProfileService,
) {

    @PostMapping(value = ["/api/fitbit/profile"])
    fun getProfile(principal: JwtAuthentication): ResponseEntity<Void> {
        val credentials = fitbitOAuth.getCredentials(principal) ?: return ResponseEntity.internalServerError().build()
        val profile = fitbitApi.profile(credentials)?.let { profileDto ->
            profileDto.user?.let { user ->
                fitbitProfileConverter.toDomain(user, principal.getUserId())
            }
        }
        profile?.let { p ->
            fitbitProfileService.update(p)
        }
        return ResponseEntity.ok().build()
    }

}