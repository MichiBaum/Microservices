package com.michibaum.authentication_service.app.basic_authentication

class InvalidCurrentPasswordException(message: String = "Current password does not match") : RuntimeException(message)

class SamePasswordException(message: String = "New password must be different from current password") : RuntimeException(message)

class UserCredentialsNotFoundException(message: String = "User credentials not found") : RuntimeException(message)
