package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.time.LocalDate
import java.util.*

@Entity
@Table(name="event")
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

    @Column(nullable = false, columnDefinition = "TEXT")
    val internalComment: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val platform: ChessPlatform,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}
