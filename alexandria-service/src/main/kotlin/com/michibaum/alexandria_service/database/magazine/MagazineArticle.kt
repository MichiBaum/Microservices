package com.michibaum.alexandria_service.database.magazine

import com.michibaum.alexandria_service.database.IdNullException
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name="magazine_article")
class MagazineArticle(

    @Column(name="title", nullable = false)
    val title: String,
    
    @ManyToOne(targetEntity = Magazine::class, fetch = FetchType.LAZY, optional = false)
    val magazine: Magazine,
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
    
) {
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}