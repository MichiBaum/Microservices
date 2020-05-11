package com.michibaum.lifemanagementbackend.core.resolver

import com.michibaum.lifemanagementbackend.core.annotations.ArgumentResolver
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import kotlin.reflect.KClass


typealias ResolveArgumentMethod = (MethodParameter, ModelAndViewContainer?, NativeWebRequest, WebDataBinderFactory?) -> Any

class CustomMethodArgumentResolver(
    private val kClass: KClass<*>,
    private val resolveArgumentMethod: ResolveArgumentMethod
) : HandlerMethodArgumentResolver {

    private val annotationIsArgumentResolver = fun(annotation: Annotation?) = annotation is ArgumentResolver
    private val paramIsAssignableFromKClass =
        fun(parameter: MethodParameter) = parameter.parameterType.isAssignableFrom(kClass.java)
    private val paramToSequence =
        fun(parameter: MethodParameter) = parameter.parameterAnnotations.iterator().asSequence()
    private val paramHasAnnotationArgumentResolver =
        fun(parameter: MethodParameter) = paramToSequence(parameter).any(annotationIsArgumentResolver)

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return paramIsAssignableFromKClass(parameter) && paramHasAnnotationArgumentResolver(parameter)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        return resolveArgumentMethod.invoke(parameter, mavContainer, webRequest, binderFactory)
    }

}
