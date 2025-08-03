package com.michibaum.authentication_service.database

import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name="basic_authentication_failure")
class BasicAuthenticationFailure(
    id: UUID? = null,
    username: String,
    date: LocalDateTime = LocalDateTime.now()
): BasicAuthenticationAttempt(
    id = id,
    date = date,
    username = username
) {
    
    companion object {
        
        fun from(from: BasicAuthenticationAttempt): BasicAuthenticationFailure {
            return BasicAuthenticationFailure(
                date = from.date,
                username = from.username,
            )
        }
        
    }
    
}