package com.michibaum.fitness_service.api.sleep

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
class SleepStage(

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val start: LocalDateTime,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val end: LocalDateTime,

    @Enumerated(EnumType.STRING)
    val stage: Stage,

    @Column(nullable = false)
    val duration: Long,

    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID(),
) {
}