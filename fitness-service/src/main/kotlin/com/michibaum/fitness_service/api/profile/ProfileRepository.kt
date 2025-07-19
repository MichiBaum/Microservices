package com.michibaum.fitness_service.api.profile

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ProfileRepository: JpaRepository<Profile, UUID> {
    fun findByUserId(userId: String): Profile?
}