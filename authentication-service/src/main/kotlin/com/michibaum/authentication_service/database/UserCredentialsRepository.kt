package com.michibaum.authentication_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserCredentialsRepository : JpaRepository<UserCredentials, UUID> {
    fun findByUsername(username: String): UserCredentials?
    fun existsByUsername(username: String): Boolean
}
