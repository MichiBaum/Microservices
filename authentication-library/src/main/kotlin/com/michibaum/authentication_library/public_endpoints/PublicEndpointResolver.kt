package com.michibaum.authentication_library.public_endpoints

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.web.bind.annotation.*
import java.lang.reflect.Method

/**
 * https://docs.spring.io/spring-security/reference/servlet/authorization/authorize-http-requests.html
 */
class PublicEndpointResolver(
    private val publicAnnotation: Class<out Annotation>,
    private vararg val packageName: String
) { // TODO take into account that @RestController can have @RequestMapping

    private val logger: Logger = LoggerFactory.getLogger(PublicEndpointResolver::class.java)
    private val staticPublicEndpoints: ArrayList<PublicEndpointDetails> = ArrayList()

    fun addStaticPublicEndpoint(endpoint: PublicEndpointDetails){
        staticPublicEndpoints.add(endpoint)
    }

    fun addStaticPublicEndpoints(endpoints: List<PublicEndpointDetails>){
        staticPublicEndpoints.addAll(endpoints)
    }

    fun run(): List<PublicEndpointDetails> {
        val mappings = getAllRestController()
            .map { getMethodsAnnotatedWith(it) }
            .map { getMappingValue(it) }
            .flatten()

        val endpoints = staticPublicEndpoints + mappings

        logger.info("Found ${endpoints.size} public endpoints")
        for (endpoint in endpoints) {
            logger.info("Mapping: ${endpoint.httpMethod} '${endpoint.rawPath}' converted to: '${endpoint.path}'")
        }
        return endpoints
    }


    private fun getMethodsAnnotatedWith(type: Class<*>): List<Method> {
        logger.debug("Searching for methods annotated with ${publicAnnotation.name} in class: ${type.name}")
        val methods: Array<Method> = type.methods
        logger.debug("Found ${methods.size} methods")
        val annotatedMethods: MutableList<Method> = ArrayList(methods.size)
        for (method in methods) {
            if (method.isAnnotationPresent(publicAnnotation)) {
                annotatedMethods.add(method)
            }
        }
        logger.debug("Found ${annotatedMethods.size} annotated methods")
        return annotatedMethods
    }

    private fun getMappingValue(methods: List<Method>): List<PublicEndpointDetails> {
        return methods.map { getMappingValue(it) }
            .flatten()
            .toList()
    }

    private fun getMappingValue(method: Method): List<PublicEndpointDetails> {
        logger.debug("Searching for mapping annotation on method: ${method.name}")

        val mappings = ArrayList<PublicEndpointDetails>()

        // Extract pattern type from PublicEndpoint annotation
        val patternType = if (method.isAnnotationPresent(PublicEndpoint::class.java)) {
            method.getAnnotation(PublicEndpoint::class.java).pattern
        } else {
            PublicPattern.ANT
        }

        logger.debug("Using pattern type: $patternType for method: ${method.name}")

        getEndpointsFromRequestMapping(method, patternType).also {
            mappings.addAll(it)
        }

        if(method.isAnnotationPresent(GetMapping::class.java)){
            val paths = method.getAnnotation(GetMapping::class.java).value
            val result = paths.map { PublicEndpointDetails(it, RequestMethod.GET, patternType) }
            mappings.addAll(result)
        }

        if(method.isAnnotationPresent(PostMapping::class.java)){
            val paths = method.getAnnotation(PostMapping::class.java).value
            val result = paths.map { PublicEndpointDetails(it, RequestMethod.POST, patternType) }
            mappings.addAll(result)
        }

        if(method.isAnnotationPresent(PutMapping::class.java)){
            val paths = method.getAnnotation(PutMapping::class.java).value
            val result = paths.map { PublicEndpointDetails(it, RequestMethod.PUT, patternType) }
            mappings.addAll(result)
        }

        if(method.isAnnotationPresent(PatchMapping::class.java)){
            val paths = method.getAnnotation(PatchMapping::class.java).value
            val result = paths.map { PublicEndpointDetails(it, RequestMethod.PATCH, patternType) }
            mappings.addAll(result)
        }

        if (method.isAnnotationPresent(DeleteMapping::class.java)) {
            val paths = method.getAnnotation(DeleteMapping::class.java).value
            val result = paths.map { PublicEndpointDetails(it, RequestMethod.DELETE, patternType) }
            mappings.addAll(result)
        }

        logger.debug("Found mapping: $mappings")
        return mappings
    }

    private fun getEndpointsFromRequestMapping(
        method: Method,
        patternType: PublicPattern
    ): List<PublicEndpointDetails> {
        if (method.isAnnotationPresent(RequestMapping::class.java)) {
            val paths = method.getAnnotation(RequestMapping::class.java).value
            val methods = method.getAnnotation(RequestMapping::class.java).method
            val result = paths.map { path ->
                methods.map { method ->
                    PublicEndpointDetails(path, method, patternType)
                }
            }.flatten()
            return result
        }
        return emptyList()
    }

    private fun getAllRestController(): List<Class<*>> {
        logger.debug("Searching for all RestController in packages: ${packageName.joinToString(", ")}")
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AnnotationTypeFilter(RestController::class.java))
        val beanDefinitions = packageName
            .map { scanner.findCandidateComponents(it) }
            .flatten()
            .filterNotNull()

        logger.debug("Found ${beanDefinitions.size} RestController")
        val classes = beanDefinitions.map { Class.forName(it.beanClassName) }.filterNotNull().toList()
        logger.debug("Resolved ${classes.size} classes of RestController")
        return classes
    }

}
