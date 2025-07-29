package com.michibaum.fitness_service.api.profile

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProfileRepository: JpaRepository<Profile, UUID> {
    fun findByUserId(userId: String): Profile?
}