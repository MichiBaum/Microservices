package com.michibaum.alexandria_service.database

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="news_author")
class Author (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){}