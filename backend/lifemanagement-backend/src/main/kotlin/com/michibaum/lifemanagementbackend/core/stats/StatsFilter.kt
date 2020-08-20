package com.michibaum.lifemanagementbackend.core.stats

import mu.KotlinLogging
import org.springframework.stereotype.Component
import javax.servlet.*
import javax.servlet.annotation.WebFilter
import javax.servlet.http.HttpServletRequest

@Component
@WebFilter("/*")
class StatsFilter : Filter {

    private val logger = KotlinLogging.logger {}

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
        // empty
    }

    override fun doFilter(req: ServletRequest, resp: ServletResponse, chain: FilterChain) {
        var time = System.currentTimeMillis()
        try {
            chain.doFilter(req, resp)
        } finally {
            time = System.currentTimeMillis() - time
            logger.trace("{}: {} ms ", (req as HttpServletRequest).requestURI, time)
        }
    }

    override fun destroy() {
        // empty
    }

}
