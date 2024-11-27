package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
class Event(

    @Column(nullable = false)
    val title: String,

    @Column(nullable = true)
    val url: String,

    @Column(nullable = true)
    val embedUrl: String,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val dateFrom: LocalDateTime,

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val dateTo: LocalDateTime,

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = EventCategory::class)
    @JoinTable(name = "EVENT_CATEGORY_MAPPING", joinColumns = [JoinColumn(name = "event_id")], inverseJoinColumns = [JoinColumn(name = "category_id")])
    val categories: MutableSet<EventCategory> = mutableSetOf(),

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Person::class)
    @JoinTable(name = "EVENT_PARTICIPANTS_MAPPING", joinColumns = [JoinColumn(name = "event_id")], inverseJoinColumns = [JoinColumn(name = "person_id")])
    val participants: Set<Person> = mutableSetOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

) {
}