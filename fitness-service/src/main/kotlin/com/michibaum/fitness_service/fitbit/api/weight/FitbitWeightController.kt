package com.michibaum.fitness_service.fitbit.api.weight

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.fitbit.FitbitOAuth
import com.michibaum.fitness_service.fitbit.api.FitbitApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FitbitWeightController(
    val fitbitApi: FitbitApi,
    val fitbitOAuth: FitbitOAuth,
    val fitbitWeightConverter: FitbitWeightConverter,
    val fitbitWeightService: FitbitWeightService,
    val fitbitWeightValidator: FitbitWeightValidator
) {

    @PostMapping(value = ["/api/fitbit/weight/{startDate}/{endDate}"])
    fun updateWeight(@PathVariable startDate: String, @PathVariable endDate: String, principal: JwtAuthentication): ResponseEntity<Void>{
        fitbitWeightValidator.validate(startDate, endDate)?.let { return it }

        val credentials = fitbitOAuth.getCredentials(principal) ?: return ResponseEntity.internalServerError().build()
        val weight = fitbitApi.weightLog(credentials, startDate, endDate).map { weight ->
            fitbitWeightConverter.toDomain(weight, principal.getUserId())
        }
        fitbitWeightService.update(weight)
        return ResponseEntity.ok().build()
    }

}