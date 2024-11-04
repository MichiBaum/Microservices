package com.michibaum.authentication_library.security.basic

fun interface CredentialsValidator {
    fun validate(basic: BasicAuthentication): Boolean
}