package com.michibaum.alexandria_service.database.magazine

import com.michibaum.alexandria_service.database.IdNullException
import jakarta.persistence.*
import jakarta.persistence.FetchType.LAZY
import java.util.*

@Entity
@Table(name="magazine_list_of_contents_element")
class MagazineListOfContentsElement (

    @ManyToOne(targetEntity = MagazineArticle::class, fetch = LAZY, optional = false)
    val article: MagazineArticle,

    @Column(name="page_number")
    val pageNumber: Int,

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "magazine_id", nullable = false)
    val magazine: Magazine,

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null
    
){
    fun idOrThrow(): UUID = id ?: throw IdNullException()
}