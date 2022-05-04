package com.michibaum.authentication_service.authentication

import lombok.AllArgsConstructor
import lombok.Data

@Data
@AllArgsConstructor
class AuthenticationDto(val username: String, val password: String)