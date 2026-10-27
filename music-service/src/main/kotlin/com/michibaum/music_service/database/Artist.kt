package com.michibaum.music_service.database

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.UuidGenerator
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name="artist")
data class Artist(

    @Column(nullable = false)
    val name: String,

    @Column(nullable = true, length = 16)
    val isni: String?,

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    val created: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    val updated: LocalDateTime? = null,

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    val id: UUID? = null
)
