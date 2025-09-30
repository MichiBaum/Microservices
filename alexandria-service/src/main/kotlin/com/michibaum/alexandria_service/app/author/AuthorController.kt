package com.michibaum.alexandria_service.app.author

import com.michibaum.authentication_library.public_endpoints.PublicEndpoint
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorController {
    
    @PublicEndpoint
    @GetMapping("/api/authors")
    fun getAllAuthors() : Nothing = 
        throw UnsupportedOperationException()
    
}