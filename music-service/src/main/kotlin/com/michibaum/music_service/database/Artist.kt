package com.michibaum.music_service.database

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name="artist")
data class Artist(

    @Column(nullable = false)
    val name: String,

    /**
     * ISNI (International Standard Name Identifier) is the ISO certified global standard number for identifying
     * the millions of contributors to creative works and those active in their distribution, including researchers,
     * inventors, writers, artists, visual creators, performers, producers, publishers, aggregators, and more.
     */
    @Column(nullable = true, length = 16)
    val isni: String?,

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    val created: LocalDateTime? = null,

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated", nullable = false)
    val updated: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
)
