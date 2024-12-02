package com.michibaum.fitness_service.api.sleep

import jakarta.persistence.*
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
open class Sleep(

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val startTime: LocalDateTime,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val endTime: LocalDateTime,

    @Column(nullable = false)
    val duration: Long,

    @OneToMany(fetch = FetchType.EAGER, targetEntity = SleepStage::class, cascade = [CascadeType.ALL])
    @JoinColumn(name = "sleep_id", referencedColumnName = "id")
    val stages: Set<SleepStage>,

    @Column(nullable = false, unique = false)
    val userId: String,

    @Id
    @UuidGenerator
    val id: UUID = UUID.randomUUID()
)