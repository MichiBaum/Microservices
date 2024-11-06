package com.michibaum.fitness_service.api.weight

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
class Weight(

    @Column(nullable = false, unique = false)
    val weight: Double,

    @Column(nullable = false, unique = false)
    val bmi: Double,

    @Column(nullable = true, unique = false)
    val fatPercentage: Int?,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val date: LocalDateTime,

    // TODO platform id like 'fitbit:84b81873-bf7e-4f20-8897-61a3ac8cf738'

    @Column(nullable = false, unique = false)
    val userId: String,

    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),
) {
}