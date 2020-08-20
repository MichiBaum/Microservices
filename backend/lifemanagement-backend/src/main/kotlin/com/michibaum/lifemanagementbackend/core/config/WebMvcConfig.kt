package com.michibaum.lifemanagementbackend.core.config

import com.michibaum.lifemanagementbackend.core.argumentresolver.CustomMethodArgumentResolver
import com.michibaum.lifemanagementbackend.user.domain.User
import com.michibaum.lifemanagementbackend.user.repository.UserRepository
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import javax.servlet.http.HttpServletResponse

@Configuration
class WebMvcConfig(
    private val userRepository: UserRepository
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(createCurrentUserMethodArgumentResolver())
    }

    private fun createCurrentUserMethodArgumentResolver(): CustomMethodArgumentResolver {
        val resolveUserArgument =
            fun(
                _: MethodParameter,
                _: ModelAndViewContainer?,
                webRequest: NativeWebRequest,
                _: WebDataBinderFactory?
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

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
    }

}
