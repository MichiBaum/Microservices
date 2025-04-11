package com.michibaum.alexandria_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="list_of_contents")
class ListOfContents (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){}