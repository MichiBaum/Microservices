package com.michibaum.lifemanagementbackend.repository

import com.michibaum.lifemanagementbackend.domain.User

interface UserRepository :
    CustomJpaRepository<User, Long> {
    fun findByName(name: String): User?
}
