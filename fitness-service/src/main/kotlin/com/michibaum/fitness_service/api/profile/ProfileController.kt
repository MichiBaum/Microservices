package com.michibaum.fitness_service.api.profile

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ProfileController(val profileService: ProfileService, val profileConverter: ProfileConverter) {

    @GetMapping("/api/profile")
    fun getProfile(principal: JwtAuthentication): ProfileDto? {
        val profile = profileService.findByUser(principal)
        return profile?.let {
            profileConverter.toDto(profile)
        }
    }

}