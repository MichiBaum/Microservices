package com.michibaum.fitness_service.fitbit.api

import com.michibaum.fitness_service.api.profile.Profile
import org.springframework.stereotype.Component

@Component
class FitbitProfileConverter {

    fun toDomain(user: UserDto, userId: String): Profile {
        return Profile(
            age = user.age.toString(),
            country = user.country,
            fullName = user.fullName,
            gender = user.gender,
            height = user.height,
            userId = userId,
        )
    }

}