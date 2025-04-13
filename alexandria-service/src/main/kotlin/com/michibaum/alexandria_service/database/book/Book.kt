package com.michibaum.alexandria_service.database.book

import com.michibaum.alexandria_service.database.Author
import com.michibaum.alexandria_service.database.ListOfContents
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="book")
class Book (

    @ManyToOne(targetEntity = Author::class, fetch = FetchType.LAZY, optional = false)
    val author: Author,

    @ManyToOne(targetEntity = ListOfContents::class, fetch = FetchType.LAZY, optional = false)
    val listOfContents: ListOfContents,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){}