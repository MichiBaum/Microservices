package com.michibaum.fitness_service.fitbit.api.profile

import org.springframework.stereotype.Component

@Component
class FitbitProfileConverter {

    fun toDomain(user: UserDto, userId: String): FitbitProfile {
        return FitbitProfile(
            age = user.age.toString(),
            country = user.country,
            fullName = user.fullName,
            gender = user.gender,
            height = user.height,
            userId = userId,
        )
    }

}