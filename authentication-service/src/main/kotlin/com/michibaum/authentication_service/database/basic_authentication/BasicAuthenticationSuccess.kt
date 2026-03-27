package com.michibaum.authentication_service.database.basic_authentication

import com.michibaum.authentication_service.database.JwtEntity
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

@Entity
@DiscriminatorValue("success")
class BasicAuthenticationSuccess(
    @Column(name = "user_id", nullable = false)
    val userId: String,

    @OneToOne(optional = true, orphanRemoval = true) // Optional & nullable true due tue inheritance
    @JoinColumn(name = "jwt_id", nullable = true)
    val jwt: JwtEntity,

    authenticationAttempt: BasicAuthenticationAttempt,
    username: String
) : BasicAuthenticationAttempt(
    authenticationAttempt = authenticationAttempt.authenticationAttempt,
    username = username
) {
    
    companion object{
        
        fun from(from: BasicAuthenticationAttempt, userId: String, jwt: JwtEntity): BasicAuthenticationSuccess {
            return BasicAuthenticationSuccess(
                userId = userId,
                username = from.username,
                authenticationAttempt = from,
                jwt = jwt,
            )
        }
        
    }
    
}