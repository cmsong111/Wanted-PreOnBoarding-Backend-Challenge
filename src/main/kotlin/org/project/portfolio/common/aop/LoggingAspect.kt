package org.project.portfolio.common.aop

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect {
    @Around("@within(org.springframework.web.bind.annotation.RestController) && within(org.project.portfolio..*)")
    fun logControllerExecution(joinPoint: ProceedingJoinPoint): Any? {
        val className = joinPoint.target.javaClass.simpleName
        val methodName = joinPoint.signature.name
        val start = System.currentTimeMillis()

        return try {
            val result = joinPoint.proceed()
            val duration = System.currentTimeMillis() - start
            logger.info(
                "API call finished: class={}, method={}",
                kv("class", className),
                kv("method", methodName),
                kv("durationMs", duration),
            )
            result
        } catch (e: Throwable) {
            val duration = System.currentTimeMillis() - start
            logger.warn { "Failed $className.$methodName - took ${duration}ms" }
            throw e
        }
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}
