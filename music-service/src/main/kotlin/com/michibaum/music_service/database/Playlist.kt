package com.michibaum.music_service.database

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "playlist")
class Playlist(
    @Column(nullable = false)
    var name: String,

    @Column(length = 2000)
    var description: String? = null,

    @Column(name = "cover_image_url", length = 1000)
    var coverImageUrl: String? = null,

    @Column(name = "created_by", nullable = false)
    val createdBy: String,

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    val created: LocalDateTime? = null,

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    var updated: LocalDateTime? = null,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Playlist) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0
}
