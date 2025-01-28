package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="event_media_link")
data class EventMediaLink (

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val url: String?,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)