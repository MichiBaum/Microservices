package com.michibaum.authentication_service.app.basic_authentication

data class AuthenticationResponse(val username: String, val jwt: String)
