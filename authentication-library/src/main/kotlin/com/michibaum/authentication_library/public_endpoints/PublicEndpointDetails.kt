package com.michibaum.authentication_library.public_endpoints

import org.springframework.http.HttpMethod
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.bind.annotation.RequestMethod

class PublicEndpointDetails(
    val rawPath: String,
    requestMethod: RequestMethod
) { // TODO add matching regex for things like uuid https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
    val antPath: String = createAntPath()
    val httpMethod: HttpMethod = HttpMethod.valueOf(requestMethod.name)
    val requestMatcher: RequestMatcher = createRequestMatcher()

    private fun createAntPath(): String {
        return rawPath.replace("\\{.*}".toRegex(), "*")
    }

    private fun createRequestMatcher(): RequestMatcher {
        return AntPathRequestMatcher.antMatcher(httpMethod, antPath)
    }

}