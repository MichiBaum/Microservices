package com.michibaum.lifemanagementbackend.core.security

import com.michibaum.lifemanagementbackend.user.domain.HttpHeader
import com.michibaum.lifemanagementbackend.user.domain.LoginLog
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.repository.HttpHeaderRepository
import com.michibaum.lifemanagementbackend.user.repository.LoginLogRepository
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class LoginLogCreator(
    private val loginLogRepository: LoginLogRepository,
    private val httpHeaderRepository: HttpHeaderRepository
) {

    fun create(user: User, request: HttpServletRequest? = null, successfullAuth: Boolean){
        val ipAddress = request?.getHeader("X-FORWARDED-FOR") ?: request?.remoteAddr ?: ""
        val clientIpAddress = if(ipAddress.contains(','))
                ipAddress.split(",")[0]
            else
                ipAddress

        val httpHeaders: MutableList<HttpHeader> = createAndGetHttpHeaders(request)

        val loginLog = LoginLog(
            user = user,
            ipAddress = clientIpAddress,
            reqMethod = request?.method ?: "",
            successfullAuth = successfullAuth,
            httpHeaders = httpHeaders
        )
        loginLogRepository.save(loginLog)
    }

    private data class Header(val name: String, val value: String)
    private fun createAndGetHttpHeaders(request: HttpServletRequest?): MutableList<HttpHeader> {
        request?.let {
            val headerPairList = it.headerNames.toList().map { headerName -> Header(headerName, request.getHeader(headerName)) }
            return headerPairList.map { headerPair ->
                httpHeaderRepository.findByNameAndValue(headerPair.name, headerPair.value)
                    ?: httpHeaderRepository.save(HttpHeader(headerPair.name, headerPair.value))
            }.toMutableList()
        }
        return mutableListOf()
    }

}
