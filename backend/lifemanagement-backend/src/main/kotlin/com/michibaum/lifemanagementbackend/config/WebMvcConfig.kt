package com.michibaum.lifemanagementbackend.config

import com.michibaum.lifemanagementbackend.domain.User
import com.michibaum.lifemanagementbackend.repository.UserRepository
import com.michibaum.lifemanagementbackend.resolver.CustomMethodArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletResponse

@Configuration
@EnableWebMvc
class WebMvcConfig(
    private val userRepository: UserRepository
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(createCurrentUserMethodArgumentResolver())
    }

    private fun createCurrentUserMethodArgumentResolver(): CustomMethodArgumentResolver {
        val resolveUserArgument =
            fun(
                parameter: MethodParameter,
                mavContainer: ModelAndViewContainer?,
                webRequest: NativeWebRequest,
                binderFactory: WebDataBinderFactory?
            ): User =
                run {
                    SecurityContextHolder.getContext().authentication?.name?.let { username ->
                        userRepository.findByName(username)?.let { user -> return user }
                    } ?: kotlin.run {
                        (webRequest.nativeResponse as HttpServletResponse).status =
                            HttpStatus.INTERNAL_SERVER_ERROR.value()
                        throw UsernameNotFoundException("User not found")
                    }
                }

        return CustomMethodArgumentResolver(User::class, resolveUserArgument)
    }

}
