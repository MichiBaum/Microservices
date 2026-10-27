package com.michibaum.music_service.database.musixmatch

import com.michibaum.music_service.database.Album
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.*

@Entity
@Table(name="album_musixmatch")
data class AlbumMusixmatch(

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name="album_id", nullable=false)
    val album: Album,

    @Column(nullable = false, unique = true)
    val musixmatchId: String,

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    val created: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated", nullable = false)
    val updated: Instant? = null,

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    val id: UUID? = null
)