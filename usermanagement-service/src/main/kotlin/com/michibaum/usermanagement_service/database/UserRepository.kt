package com.michibaum.usermanagement_service.database

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsername(username: String): User?
    fun existsByUsernameOrEmail(username: String, email: String): Boolean
}