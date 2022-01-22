package com.michibaum.authentication_service.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationDto {
    private String username;
    private String password;
}
