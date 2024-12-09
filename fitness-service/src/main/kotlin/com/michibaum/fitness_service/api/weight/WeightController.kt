package com.michibaum.fitness_service.api.weight

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WeightController(
    private val weightService: WeightService,
    private val weightConverter: WeightConverter
) {

    @GetMapping(value = ["/api/weight"])
    fun getWeight(principal: JwtAuthentication) =
        weightService.getByUser(principal.getUserId())
            .map { weightConverter.toDto(it) }

}