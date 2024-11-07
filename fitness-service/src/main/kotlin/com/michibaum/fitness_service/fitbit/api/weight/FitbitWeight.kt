package com.michibaum.fitness_service.fitbit.api.weight

import com.michibaum.fitness_service.api.weight.Weight
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import java.time.LocalDateTime

@Entity
@Inheritance(strategy= InheritanceType.SINGLE_TABLE)
class FitbitWeight(
    weight: Double,
    bmi: Double,
    fatPercentage: Int?,
    date: LocalDateTime,
    userId: String,

    @Column(nullable = false, unique = true)
    val fitbitId: Long,

) : Weight(
    weight = weight,
    bmi = bmi,
    fatPercentage = fatPercentage,
    date = date,
    userId = userId
) {
}