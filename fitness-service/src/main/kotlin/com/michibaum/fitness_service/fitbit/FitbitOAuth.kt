package com.michibaum.fitness_service.fitbit

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.fitbit.oauth.FitbitOAuthCredentials

interface FitbitOAuth {

    /**
     * Retrieves the OAuth credentials associated with the given JWT-authenticated principal.
     *
     * @param principal The authenticated principal containing JWT information.
     * @return The corresponding Fitbit OAuth credentials or null if no credentials are found.
     */
    fun getCredentials(principal: JwtAuthentication): FitbitOAuthCredentials?

}