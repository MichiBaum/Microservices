package com.michibaum.authentication_library

import org.springframework.cloud.openfeign.FeignClient

@FeignClient(value = "authentication-service")
interface AuthenticationClient : AuthenticationEndpoints