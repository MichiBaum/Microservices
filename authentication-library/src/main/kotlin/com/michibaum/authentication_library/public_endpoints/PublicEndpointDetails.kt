package com.michibaum.authentication_library.public_endpoints

import org.springframework.http.HttpMethod
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RegexRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.bind.annotation.RequestMethod

class PublicEndpointDetails(
    val rawPath: String,
    requestMethod: RequestMethod,
    val patternType: PublicPattern = PublicPattern.ANT
) {
    val path: String = createPath()
    val httpMethod: HttpMethod = HttpMethod.valueOf(requestMethod.name)
    val requestMatcher: RequestMatcher = createRequestMatcher()

    private fun createPath(): String {
        return when (patternType) {
            PublicPattern.UUID -> rawPath.replace("\\{.*?}".toRegex(), PublicPattern.UUID.replacement)
            PublicPattern.ANT -> rawPath.replace("\\{.*}".toRegex(), "*")
        }
    }

    private fun createRequestMatcher(): RequestMatcher {
        return when (patternType) {
            PublicPattern.ANT -> AntPathRequestMatcher.antMatcher(httpMethod, path)
            PublicPattern.UUID -> RegexRequestMatcher.regexMatcher(httpMethod, path)
        }
    }
}
