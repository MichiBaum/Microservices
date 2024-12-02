package com.michibaum.fitness_service.fitbit.api.profile

import com.michibaum.fitness_service.api.profile.Profile
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType

@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
class FitbitProfile(
    age: String,
    country: String,
    fullName: String,
    gender: String,
    height: Double,
    userId: String
) : Profile(
    age = age,
    country = country,
    fullName = fullName,
    gender = gender,
    height = height,
    userId = userId
)