package com.michibaum.gatewayservice

import org.springframework.web.server.ServerWebExchange

fun requestLog(exchange: ServerWebExchange?): String =
    """
        Request:
        ID: ${exchange?.request?.id}
        Uri: ${exchange?.request?.uri}
        Url: ${exchange?.request?.uri?.toURL()}
        Headers: ${exchange?.request?.headers}
        
        Response:
        Status code: ${exchange?.response?.statusCode}
        Headers: ${exchange?.response?.headers}
        Is committed: ${exchange?.response?.isCommitted}
    """.trimIndent()