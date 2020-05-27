package com.michibaum.lifemanagementbackend.publicendpoint

import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.reflect.Method

class PublicEndpointSearcher(vararg restControllerPackages: String) {

    private val restControllerPackages: List<String> = listOf(*restControllerPackages)

    fun getMethodsAnnotatedWith(type: Class<*>, annotation: Class<PublicEndpoint>): List<Method> =
        type.methods.filter { it.isAnnotationPresent(annotation) }

    fun getRequestMappingValue(method: Method): List<PublicEndpointDetails> {
        return arrayListOf(
            PublicEndpointDetails(
                listOf(*method.getAnnotation(RequestMapping::class.java).value),
                method.getAnnotation(PublicEndpoint::class.java).character,
                method.getAnnotation(PublicEndpoint::class.java).numerus
            )
        )
    }

    private val scanner = ClassPathScanningCandidateComponentProvider(false).also {
        it.addIncludeFilter(
            AnnotationTypeFilter(RestController::class.java)
        )
    }

    val allRestController: List<Class<*>>
        get() =
            restControllerPackages
                .map { basePackage: String -> scanner.findCandidateComponents(basePackage) }
                .flatten()
                .map { beanDefinition: BeanDefinition ->
                    Class.forName(beanDefinition.beanClassName)
                }

}
