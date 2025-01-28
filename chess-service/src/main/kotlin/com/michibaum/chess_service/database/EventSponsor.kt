package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "event_sponsor")
data class EventSponsor (



    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)
