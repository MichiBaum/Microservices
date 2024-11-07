package com.michibaum.fitness_service.api.profile

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import org.springframework.stereotype.Service

@Service
class ProfileService(val profileRepository: ProfileRespository) {

    fun findByUser(principal: JwtAuthentication): Profile? =
        profileRepository.findByUserId(principal.getUserId())

}