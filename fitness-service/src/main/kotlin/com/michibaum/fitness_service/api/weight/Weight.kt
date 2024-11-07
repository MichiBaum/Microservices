package com.michibaum.fitness_service.api.weight

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
open class Weight(

    @Column(nullable = false, unique = false)
    val weight: Double,

    @Column(nullable = false, unique = false)
    val bmi: Double,

    @Column(nullable = true, unique = false)
    val fatPercentage: Int?,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val date: LocalDateTime,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),
) {
}