package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="event_category")
class EventCategory(
    @Column(nullable = false)
    val name: String,

    @Column(nullable = false)
    val description: String,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}