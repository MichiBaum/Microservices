package com.michibaum.chess_service.database

import jakarta.persistence.*
import java.io.Serializable
import java.util.UUID

@Entity
@Table(name = "event_category_mapping")
@IdClass(EventCategoryMappingId::class)
class EventCategoryMapping(
    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    val event: Event,

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    val category: EventCategory
)

class EventCategoryMappingId(
    var event: UUID? = null,
    var category: UUID? = null
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EventCategoryMappingId) return false

        if (event != other.event) return false
        if (category != other.category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = event?.hashCode() ?: 0
        result = 31 * result + (category?.hashCode() ?: 0)
        return result
    }
}
