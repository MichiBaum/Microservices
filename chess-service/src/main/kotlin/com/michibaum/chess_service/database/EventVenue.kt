package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="event_venue")
data class EventVenue(


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

)

enum class EventVenueType {
    Online,
    Onsite
}