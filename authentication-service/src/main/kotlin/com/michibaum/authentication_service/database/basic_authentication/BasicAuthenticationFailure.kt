package com.michibaum.authentication_service.database.basic_authentication

import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity

@Entity
@DiscriminatorValue("failure")
class BasicAuthenticationFailure(
    authenticationAttempt: BasicAuthenticationAttempt,
    username: String
) : BasicAuthenticationAttempt(
    authenticationAttempt = authenticationAttempt.authenticationAttempt,
    username = username
) {
    
    companion object {
        
        fun from(from: BasicAuthenticationAttempt): BasicAuthenticationFailure {
            return BasicAuthenticationFailure(
                username = from.username,
                authenticationAttempt = from,
            )
        }
        
    }
    
}