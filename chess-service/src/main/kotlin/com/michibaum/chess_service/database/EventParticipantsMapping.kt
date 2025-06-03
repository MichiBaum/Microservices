package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "event_participants_mapping")
class EventParticipantsMapping(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    val event: Event,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    val person: Person,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)