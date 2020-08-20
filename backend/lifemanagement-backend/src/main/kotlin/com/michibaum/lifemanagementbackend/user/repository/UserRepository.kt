package com.michibaum.lifemanagementbackend.user.repository

import com.michibaum.lifemanagementbackend.core.repository.CustomJpaRepository
import com.michibaum.lifemanagementbackend.user.domain.User

interface UserRepository : CustomJpaRepository<User, Long> {
    fun findByName(name: String): User?
}
