package com.michibaum.fitness_service.fitbit.api.profile

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.api.profile.ProfileService
import com.michibaum.fitness_service.fitbit.FitbitOAuth
import com.michibaum.fitness_service.fitbit.api.FitbitApi
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController(value = "fitbitProfileController")
class ProfileController(
    val fitbitApi: FitbitApi,
    val fitbitOAuth: FitbitOAuth,
    val fitbitProfileConverter: FitbitProfileConverter,
    val profileService: ProfileService
) {

    @PostMapping(value = ["/api/fitbit/profile"])
    fun getProfile(principal: JwtAuthentication) {
        val credentials = fitbitOAuth.getCredentials(principal)
        credentials?.let {
            val profile = fitbitApi.profile(it)?.let { profileDto ->
                profileDto.user?.let { user ->
                    fitbitProfileConverter.toDomain(user, principal.getUserId())
                }
            }
            profile?.let { p ->
                profileService.update(p)
            }

        }
    }

}