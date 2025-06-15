package com.michibaum.alexandria_service.database.note

import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "note_category_mapping")
@IdClass(NoteCategoryMappingId::class)
class NoteCategoryMapping(

    @Id
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "note_id", nullable = false)
    val note: Note,

    @Id
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    val noteCategory: NoteCategory

)

class NoteCategoryMappingId(
    var note: UUID? = null,
    var category: UUID? = null
) : java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is NoteCategoryMappingId) return false

        if (note != other.note) return false
        if (category != other.category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = note?.hashCode() ?: 0
        result = 31 * result + (category?.hashCode() ?: 0)
        return result
    }
}