package com.michibaum.fitness_service.api.sleep

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SleepController(
    private val sleepService: SleepService,
    private val sleepConverter: SleepConverter
) {

    @GetMapping(value = ["/api/sleep"])
    fun getSleep(principal: JwtAuthentication): List<SleepDto> =
        sleepService.getByUser(principal.getUserId())
            .map { sleepConverter.toDto(it) }

}