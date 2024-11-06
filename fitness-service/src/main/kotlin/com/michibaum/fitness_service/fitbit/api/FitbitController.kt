package com.michibaum.fitness_service.fitbit.api

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.api.profile.ProfileService
import com.michibaum.fitness_service.api.weight.WeightService
import com.michibaum.fitness_service.fitbit.FitbitOAuth
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FitbitController(
    val fitbitApi: FitbitApi,
    val fitbitOAuth: FitbitOAuth,
    val fitbitWeightConverter: FitbitWeightConverter,
    val fitbitProfileConverter: FitbitProfileConverter,
    val weightService: WeightService,
    val profileService: ProfileService
) {

    @PostMapping(value = ["/api/fitbit/weight/{startDate}/{endDate}"])
    fun updateWeight(@PathVariable startDate: String, @PathVariable endDate: String, principal: JwtAuthentication){
        val credentials = fitbitOAuth.getCredentials(principal)
        credentials?.let {
            val weight = fitbitApi.weightLog(it, startDate, endDate).map { weight ->
                fitbitWeightConverter.toDomain(weight, principal.getUserId())
            }
            weightService.update(weight)
        }
    }

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