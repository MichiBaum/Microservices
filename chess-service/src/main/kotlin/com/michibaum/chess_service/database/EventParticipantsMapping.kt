package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "event_participants_mapping")
@IdClass(EventParticipantsMappingId::class)
class EventParticipantsMapping(
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    val event: Event,

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "person_id", nullable = false)
    val person: Person
)

class EventParticipantsMappingId(
    var event: UUID? = null,
    var person: UUID? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EventParticipantsMappingId) return false

        if (event != other.event) return false
        if (person != other.person) return false

        return true
    }

    override fun hashCode(): Int {
        var result = event?.hashCode() ?: 0
        result = 31 * result + (person?.hashCode() ?: 0)
        return result
    }
}
