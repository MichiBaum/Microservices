package com.michibaum.gatewayservice.config

import org.springframework.cloud.gateway.server.mvc.common.MvcUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.server.PathContainer
import org.springframework.util.Assert
import org.springframework.web.servlet.function.RequestPredicate
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import org.springframework.web.util.pattern.PathPattern
import org.springframework.web.util.pattern.PathPatternParser
import java.util.function.Function

/**
 * Quick fix for:
 * - https://github.com/MichiBaum/Microservices/issues/170
 * - https://github.com/spring-cloud/spring-cloud-gateway/issues/3699
 */
class Http2AwareHostPatternPredicate(private val pattern: PathPattern): RequestPredicate {

    override fun test(request: ServerRequest): Boolean {
        val host = request.getHostHeader() ?: throw MissingHostHeaderException("Missing 'Host' or ':authority' header")

        val pathContainer = PathContainer.parsePath(host, PathContainer.Options.MESSAGE_ROUTE)
        val info = pattern.matchAndExtract(pathContainer)
        if (info != null) {
            MvcUtils.putUriTemplateVariables(request, info.uriVariables)
            return true
        } else {
            return false
        }

    }

}

fun ServerRequest.getHostHeader(): String? {
    val host = this.headers().header(HttpHeaders.HOST).firstOrNull()
    if(host != null) // Host header prio over :authority header
        return host

    val authority = this.headers().header(":authority").firstOrNull()

    if (authority != null)
        return authority

    return null
}

class MissingHostHeaderException(message: String) : RuntimeException(message)

class Http2AwareHostPatternParser: PathPatternParser() {

    init {
        super.setPathOptions(PathContainer.Options.MESSAGE_ROUTE)
    }

    @Suppress("deprecation")
    override fun setMatchOptionalTrailingSeparator(matchOptionalTrailingSeparator: Boolean) {
        raiseError()
    }

    override fun setCaseSensitive(caseSensitive: Boolean) {
        raiseError()
    }

    override fun setPathOptions(pathOptions: PathContainer.Options) {
        raiseError()
    }

    private fun raiseError() {
        throw UnsupportedOperationException("This is a read-only, shared instance that cannot be modified")
    }
}

val DEFAULT_HOST_INSTANCE: PathPatternParser = Http2AwareHostPatternParser();

fun hostHttp2Aware(pattern: String): RequestPredicate {
    Assert.notNull(pattern, "'pattern' must not be null")
    return hostPredicates(DEFAULT_HOST_INSTANCE).apply(pattern)
}

fun hostPredicates(patternParser: PathPatternParser): Function<String, RequestPredicate> {
    Assert.notNull(patternParser, "PathPatternParser must not be null")
    return Function { pattern: String ->
        Http2AwareHostPatternPredicate(
            patternParser.parse(
                pattern
            )
        )
    }
}