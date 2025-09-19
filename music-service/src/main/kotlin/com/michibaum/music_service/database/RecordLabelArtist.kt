package com.michibaum.music_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "record_label_artist_mapping")
@IdClass(RecordLabelArtistMappingId::class)
data class RecordLabelArtistMapping(
    
    // TODO joined (date the artist joined the record label)
    // TODO left (date the artist left the record label or null if still active)

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created", nullable = false)
    val created: LocalDateTime? = null,

    @Id
    @ManyToOne(fetch= FetchType.EAGER, optional = false)
    @JoinColumn(name="record_label_id", nullable=false)
    val recordLabel: RecordLabel,
    
    @Id
    @ManyToOne(fetch= FetchType.EAGER, optional = false)
    @JoinColumn(name="artist_id", nullable=false)
    val artist: Artist
)

class RecordLabelArtistMappingId(
    var recordLabel: UUID? = null,
    var artist: UUID? = null
): Serializable {
    
}