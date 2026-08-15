package com.michibaum.admin_service.app.domain

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/hosttech")
class HosttechRestController(
    private val hosttechDomainService: HosttechDomainService
) {
    @GetMapping("/zones")
    fun allZones(
        @RequestHeader("Authorization") authHeader: String
    ) = hosttechDomainService.getAllZones(extractToken(authHeader))
    
    @PostMapping("/remove-acme-challenge-records")
    fun removeAllAcmeChallengeRecords(
        @RequestHeader("Authorization") authHeader: String
    ) = hosttechDomainService.removeAllAcmeChallengeRecords(extractToken(authHeader))

    private fun extractToken(authHeader: String): String =
        authHeader.removePrefix("Bearer ").trim()
    
}
