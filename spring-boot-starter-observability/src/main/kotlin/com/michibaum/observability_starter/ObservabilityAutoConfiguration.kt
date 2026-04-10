package com.michibaum.observability_starter

import io.micrometer.context.ContextSnapshot
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.aop.ObservedAspect
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.core.task.TaskDecorator

@AutoConfiguration
@ConditionalOnClass(ObservedAspect::class)
class ObservabilityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(ObservationRegistry::class)
    fun observedAspect(observationRegistry: ObservationRegistry): ObservedAspect {
        return ObservedAspect(observationRegistry)
    }

    @Bean
    fun serviceObservationAspect(observationRegistry: ObservationRegistry): ServiceObservationAspect {
        return ServiceObservationAspect(observationRegistry)
    }

    @Bean
    @ConditionalOnClass(ContextSnapshot::class)
    fun contextPropagatingTaskDecorator(): TaskDecorator {
        return TaskDecorator { runnable ->
            val snapshot = ContextSnapshot.captureAll()
            Runnable {
                snapshot.setThreadLocals().use {
                    runnable.run()
                }
            }
        }
    }

}

@Aspect
class ServiceObservationAspect(private val observationRegistry: ObservationRegistry) {

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    fun beanAnnotatedWithService() {
    }

    @Pointcut("execution(public * *(..))")
    fun publicMethod() {
    }

    @org.aspectj.lang.annotation.Around("beanAnnotatedWithService() && publicMethod()")
    fun observeServiceMethod(pjp: org.aspectj.lang.ProceedingJoinPoint): Any? {
        val name = pjp.signature.declaringType.simpleName + "." + pjp.signature.name
        return io.micrometer.observation.Observation.createNotStarted(name, observationRegistry).observe {
            pjp.proceed()
        }
    }
}
