package com.michibaum.usermanagement_service.core.domain

import lombok.NoArgsConstructor
import lombok.AllArgsConstructor
import javax.persistence.GeneratedValue
import org.hibernate.annotations.GenericGenerator
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

@Entity
@NoArgsConstructor
@AllArgsConstructor
class User {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    private val id: String? = null

    @Column(nullable = false, unique = true)
    private val username: String? = null

    @Column(nullable = false, unique = true)
    private val email: String? = null

    @Column(nullable = false)
    private val password: String? = null
}