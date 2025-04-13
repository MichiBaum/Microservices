package com.michibaum.alexandria_service.database.news

import com.michibaum.alexandria_service.database.Author
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="news_article")
class NewsArticle (

    @ManyToOne(targetEntity = Author::class, fetch = FetchType.LAZY, optional = false)
    val author: Author,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){}