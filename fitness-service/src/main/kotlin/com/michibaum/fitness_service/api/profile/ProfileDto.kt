package com.michibaum.fitness_service.api.profile

data class ProfileDto(
    val age: String,
    val country: String,
    val fullName: String,
    val gender: String,
    val height: String
) {
}