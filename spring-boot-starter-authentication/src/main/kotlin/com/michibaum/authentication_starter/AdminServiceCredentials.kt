<<<<<<<< HEAD:spring-boot-starter-authentication/src/main/kotlin/com/michibaum/authentication_starter/AdminServiceCredentials.kt
package com.michibaum.authentication_starter
========
package com.michibaum.music_service.config.security
>>>>>>>> 169a530 (Refactor package structure under `apis` and `config`.):music-service/src/main/kotlin/com/michibaum/music_service/config/security/AdminServiceCredentials.kt

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.boot.admin.service")
data class AdminServiceCredentials(
    val username: String,
    val password: String
)