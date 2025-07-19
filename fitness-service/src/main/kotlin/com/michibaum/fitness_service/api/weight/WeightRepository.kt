package com.michibaum.fitness_service.api.weight

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface WeightRepository: JpaRepository<Weight, UUID> {
    fun findAllByUserId(userId: String): List<Weight>
}