package com.michibaum.alexandria_service.database.magazine

import com.michibaum.alexandria_service.database.ListOfContents
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="magazine")
class Magazine (

    @ManyToOne(targetEntity = ListOfContents::class, fetch = FetchType.LAZY, optional = false)
    val listOfContents: ListOfContents,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){}