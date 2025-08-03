package com.michibaum.authentication_service.database

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name="authentication_success")
class BasicAuthenticationSuccess(
    
    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "jwt", nullable = false, columnDefinition = "MEDIUMTEXT")
    val jwt: String,
    
    username: String,
    id: UUID? = null,
    date: LocalDateTime = LocalDateTime.now()
): BasicAuthenticationAttempt(
    id = id,
    date = date,
    username = username
) {
    
    companion object{
        
        fun from(from: BasicAuthenticationAttempt, userId: String, jwt: String): BasicAuthenticationSuccess {
            return BasicAuthenticationSuccess(
                username = from.username,
                userId = userId,
                jwt = jwt,
                date = from.date,
            )
        }
        
    }
    
}