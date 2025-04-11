package com.michibaum.alexandria_service.database.news

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="news_tag")
class NewsTag (

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){}