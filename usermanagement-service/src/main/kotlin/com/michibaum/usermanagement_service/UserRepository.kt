package com.michibaum.usermanagement_service

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByUsername(username: String): User?
    fun existsByUsernameOrEmail(username: String, email: String): Boolean
}