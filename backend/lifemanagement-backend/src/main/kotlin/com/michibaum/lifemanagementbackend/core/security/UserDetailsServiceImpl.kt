package com.michibaum.lifemanagementbackend.core.security

import com.michibaum.lifemanagementbackend.user.repository.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user =
            userRepository.findByName(username) ?: throw UsernameNotFoundException("Username $username not found")
        if (!user.enabled) {
            throw Exception("User $username disabled but tried to login")
        }
        val authorities = user.permissions.map { SimpleGrantedAuthority(it.name.name) }
        return User(user.name, user.password, authorities)
    }
}
