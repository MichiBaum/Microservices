package com.michibaum.chess_service.app.account

import com.michibaum.chess_service.database.Account
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification

data class SearchAccountDto(
    val accountName: String? = null,
    val searchLocation: SearchLocation = SearchLocation.LOCAL
){
    fun getSpecification(): Specification<Account> =
        Specification {
                root: Root<Account>, _: CriteriaQuery<*>?, builder: CriteriaBuilder ->
                builder.or(*getPredicates(root, builder).toTypedArray())
        }

    private fun getPredicates(root: Root<Account>, builder: CriteriaBuilder): List<Predicate> {
        val specifications = mutableListOf<Predicate>()

        if (!accountName.isNullOrBlank()) {
            val searchValue = "%${accountName.lowercase()}%"
            specifications.add(
                builder.like(
                    builder.lower(root.get("username")),
                    searchValue
                )
            )
        }

        return specifications
    }
}
