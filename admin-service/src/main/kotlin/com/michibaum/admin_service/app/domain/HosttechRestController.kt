package com.michibaum.admin_service.app.domain

import com.michibaum.admin_service.app.domain.dto.ZoneDto
import com.michibaum.admin_service.app.domain.dto.toDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/hosttech")
class HosttechRestController(
    private val hosttechDomainService: HosttechDomainService
) {
    @GetMapping("/zones")
    fun allZones(
        @RequestParam("Hosttech-Token") hosttechToken: String
    ): List<ZoneDto> {
        return hosttechDomainService.getAllZones(hosttechToken).map { zone ->
            zone.toDto()
        }
    }
    
    @PostMapping("/remove-acme-challenge-records")
    fun removeAllAcmeChallengeRecords(
        @RequestParam("Hosttech-Token") hosttechToken: String
    ) {
        hosttechDomainService.removeAllAcmeChallengeRecords(hosttechToken)
    }

    
}
