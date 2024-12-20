package com.michibaum.chess_service.app.event

import com.michibaum.chess_service.domain.Event
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification

data class SearchEventDto(
    val title: String? = null,
    val category: String? = null,
    val location: String? = null,
    val url: Boolean? = null,
    val embedUrl: Boolean? = null,
    val pageNumber: Int,
    val pageSize: Int
){
    fun getSpecification(): Specification<Event> =
        Specification {
            root: Root<Event>, query: CriteriaQuery<*>?, builder: CriteriaBuilder ->
                builder.and(*getPredicates(root, builder).toTypedArray())
        }

    private fun getPredicates(root: Root<Event>, builder: CriteriaBuilder): List<Predicate> {
        val specifications = mutableListOf<Predicate>()

        if (!title.isNullOrEmpty()) {
            specifications.add(builder.like(root.get("title"), title))
        }
        if (!category.isNullOrEmpty()) {
            specifications.add(builder.like(root.get("categories.name"), category))
        }
        if (!location.isNullOrEmpty()) {
            specifications.add(builder.like(root.get("location"), location))
        }
        // TODO url & embedUrl
        return specifications
    }

    fun getPageable(): PageRequest {
        return PageRequest.of(pageNumber, pageSize)
    }
}