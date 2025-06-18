package com.michibaum.alexandria_service.database.book

import com.michibaum.alexandria_service.database.IdNullException
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import java.util.*

@Entity
@Table(name="book_list_of_contents_element")
class BookListOfContentsElement (

    @Column(name="title")
    val title: String,
    
    @Column(name="page_number")
    val pageNumber: Int,

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    val book: Book,
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
    
){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}