package com.michibaum.chess_service.domain

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
class Event(

    @Column(nullable = false)
    val title: String,

    @Column(nullable = true)
    val location: String?,

    @Column(nullable = true)
    val url: String?,

    @Column(nullable = true)
    val embedUrl: String?,

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    val dateFrom: LocalDate,

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    val dateTo: LocalDate,

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = EventCategory::class)
    @JoinTable(name = "EVENT_CATEGORY_MAPPING", joinColumns = [JoinColumn(name = "event_id")], inverseJoinColumns = [JoinColumn(name = "category_id")])
    val categories: Set<EventCategory> = setOf(),

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Person::class)
    @JoinTable(name = "EVENT_PARTICIPANTS_MAPPING", joinColumns = [JoinColumn(name = "event_id")], inverseJoinColumns = [JoinColumn(name = "person_id")])
    val participants: Set<Person> = setOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}