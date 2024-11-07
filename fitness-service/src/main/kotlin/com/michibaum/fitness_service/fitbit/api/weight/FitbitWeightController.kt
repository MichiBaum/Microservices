package com.michibaum.fitness_service.fitbit.api.weight

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.fitbit.FitbitOAuth
import com.michibaum.fitness_service.fitbit.api.FitbitApi
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FitbitWeightController(
    val fitbitApi: FitbitApi,
    val fitbitOAuth: FitbitOAuth,
    val fitbitWeightConverter: FitbitWeightConverter,
    val fitbitWeightService: FitbitWeightService,
) {

    @PostMapping(value = ["/api/fitbit/weight/{startDate}/{endDate}"])
    fun updateWeight(@PathVariable startDate: String, @PathVariable endDate: String, principal: JwtAuthentication){
        val credentials = fitbitOAuth.getCredentials(principal)
        credentials?.let {
            val weight = fitbitApi.weightLog(it, startDate, endDate).map { weight ->
                fitbitWeightConverter.toDomain(weight, principal.getUserId())
            }
            fitbitWeightService.update(weight)
        }
    }

}