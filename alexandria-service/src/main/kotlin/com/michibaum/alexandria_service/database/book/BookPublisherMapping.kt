package com.michibaum.alexandria_service.database.book

import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "book_publisher_mapping")
@IdClass(BookPublisherMappingId::class)
class BookPublisherMapping(

    @Id
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    val book: Book,

    @Id
    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    val publisher: BookPublisher

)

class BookPublisherMappingId(
    var book: UUID? = null,
    var publisher: UUID? = null
) : java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BookPublisherMappingId) return false

        if (book != other.book) return false
        if (publisher != other.publisher) return false

        return true
    }

    override fun hashCode(): Int {
        var result = book?.hashCode() ?: 0
        result = 31 * result + (publisher?.hashCode() ?: 0)
        return result
    }
}