package com.michibaum.alexandria_service.database.news

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="news_category")
class NewsCategory (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){}