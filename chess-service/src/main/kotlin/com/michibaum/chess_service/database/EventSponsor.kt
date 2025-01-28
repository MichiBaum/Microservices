package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "event_sponsor")
data class EventSponsor (

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true)
    val url: String?,

    @ManyToOne(targetEntity = Event::class, fetch = FetchType.LAZY, optional = true, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinColumn(name="event_id", nullable=true)
    val event: Event,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)
