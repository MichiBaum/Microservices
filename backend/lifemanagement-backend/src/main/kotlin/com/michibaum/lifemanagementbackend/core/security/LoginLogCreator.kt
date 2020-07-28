package com.michibaum.lifemanagementbackend.core.security

import com.michibaum.lifemanagementbackend.user.domain.LoginLog
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.repository.LoginLogRepository
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class LoginLogCreator(
    private val loginLogRepository: LoginLogRepository
) {

    fun create(user: User, request: HttpServletRequest? = null, successfullAuth: Boolean){
        val ipAddress = request?.getHeader("X-FORWARDED-FOR") ?: request?.remoteAddr ?: ""
        val clientIpAddress = if(ipAddress.contains(','))
                ipAddress.split(",")[0]
            else
                ipAddress

        val loginLog = LoginLog(
            user = user,
            ipAddress = clientIpAddress,
            reqMethod = request?.method ?: "",
            successfullAuth = successfullAuth
        )
        loginLogRepository.saveAndFlush(loginLog)
    }

}
