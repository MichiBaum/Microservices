package com.michibaum.alexandria_service.database.blog

import com.michibaum.alexandria_service.database.IdNullException
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name="blog_article")
class BlogArticle(
    @Column(name = "title", nullable = false)
    val title: String,
    
    @Column(name = "content", nullable = false)
    val content: String,
    
    @Column(name = "author_id", nullable = false)
    val authorId: String,
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}