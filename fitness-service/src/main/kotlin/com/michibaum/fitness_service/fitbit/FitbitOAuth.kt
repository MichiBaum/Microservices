package com.michibaum.fitness_service.fitbit

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.fitbit.oauth.FitbitOAuthCredentials

interface FitbitOAuth {

    fun refreshToken(principal: JwtAuthentication): FitbitOAuthCredentials?
    fun getCredentials(principal: JwtAuthentication): FitbitOAuthCredentials?

}