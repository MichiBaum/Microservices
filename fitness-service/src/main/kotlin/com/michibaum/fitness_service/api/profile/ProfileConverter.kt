package com.michibaum.fitness_service.api.profile

import org.springframework.stereotype.Component

@Component
class ProfileConverter {

    fun toDto(profile: Profile) =
        ProfileDto(
            age = profile.age,
            country = profile.country,
            fullName = profile.fullName,
            gender = profile.gender,
            height = profile.height.toString()
        )

}
