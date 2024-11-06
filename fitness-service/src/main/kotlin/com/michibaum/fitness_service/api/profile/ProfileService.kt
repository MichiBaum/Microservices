package com.michibaum.fitness_service.api.profile

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.stereotype.Service

@Service
class ProfileService(val profileRepository: ProfileRespository) {

    fun update(profile: Profile) {
        val existingProfile = profileRepository.findByUserId(profile.userId)
        if (existingProfile != null) {
            profileRepository.delete(existingProfile)
        }

        profileRepository.save(profile)
    }

    fun findByUser(principal: JwtAuthentication): Profile? =
        profileRepository.findByUserId(principal.getUserId())

}