package com.michibaum.fitness_service.fitbit.api.sleep

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.fitbit.FitbitOAuth
import com.michibaum.fitness_service.fitbit.api.FitbitApi
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class FitbitSleepController(
    private val fitbitApi: FitbitApi,
    private val fitbitOAuth: FitbitOAuth,
    private val fitbitSleepConverter: FitbitSleepConverter,
    private val fitbitSleepService: FitbitSleepService,
    private val fitbitSleepValidator: FitbitSleepValidator
) {

    @PostMapping(value = ["/api/fitbit/sleep/{startDate}/{endDate}"])
    fun updateSleep(@PathVariable startDate: String, @PathVariable endDate: String, principal: JwtAuthentication): ResponseEntity<Void> {
        fitbitSleepValidator.validate(startDate, endDate)?.let { return it }

        val credentials = fitbitOAuth.getCredentials(principal) ?: return ResponseEntity.internalServerError().build()
        val sleepData = fitbitApi.sleepLog(credentials, startDate, endDate)
            .map { sleep -> fitbitSleepConverter.toDomain(sleep, principal.getUserId()) }
        fitbitSleepService.update(sleepData)
        return ResponseEntity.ok().build()
    }

}