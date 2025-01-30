package com.michibaum.gatewayservice.config.feign

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(value = "chess-service")
interface ChessClient{
    @GetMapping(value = ["/api/events"])
    fun events(): List<EventDto>
    @GetMapping(value = ["/api/openings"])
    fun openings(): List<OpeningDto>
}

data class EventDto(val id: String)
data class OpeningDto(val id: String)