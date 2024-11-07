package com.michibaum.fitness_service.fitbit

import com.michibaum.authentication_library.security.jwt.JwtAuthentication
import com.michibaum.fitness_service.fitbit.oauth.FitbitOAuthCredentials

interface FitbitOAuth {

    /**
     * Refreshes the OAuth token for a given principal.
     *
     * @param principal The authenticated principal containing JWT information.
     * @return The updated Fitbit OAuth credentials or null if the refresh fails.
     */
    fun refreshToken(principal: JwtAuthentication): FitbitOAuthCredentials?


    /**
     * Retrieves the OAuth credentials associated with the given JWT-authenticated principal.
     *
     * @param principal The authenticated principal containing JWT information.
     * @return The corresponding Fitbit OAuth credentials or null if no credentials are found.
     */
    fun getCredentials(principal: JwtAuthentication): FitbitOAuthCredentials?

}