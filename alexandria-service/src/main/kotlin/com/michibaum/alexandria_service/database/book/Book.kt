package com.michibaum.alexandria_service.database.book

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="book")
class Book (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){}