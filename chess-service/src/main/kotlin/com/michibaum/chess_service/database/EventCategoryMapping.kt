package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "event_category_mapping")
class EventCategoryMapping(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    val event: Event,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    val category: EventCategory,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)