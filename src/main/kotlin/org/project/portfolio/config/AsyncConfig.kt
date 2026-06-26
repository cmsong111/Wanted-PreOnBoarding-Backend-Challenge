package org.project.portfolio.config

import io.github.oshai.kotlinlogging.KotlinLogging
import org.slf4j.MDC
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.TaskDecorator
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer {
    @Bean(name = ["taskExecutor"])
    override fun getAsyncExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 4
            maxPoolSize = 16
            queueCapacity = 100
            setThreadNamePrefix("async-")
            setTaskDecorator(MdcTaskDecorator())
            setWaitForTasksToCompleteOnShutdown(true)
            setAwaitTerminationSeconds(30)
            initialize()
        }
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return AsyncUncaughtExceptionHandler { ex, method, params ->
            logger.error(ex) {
                "Uncaught async error in ${method.declaringClass.simpleName}.${method.name}(${params.joinToString()})"
            }
        }
    }

    /** 부모 스레드의 MDC(TRACE_ID, requestId 등)를 비동기 스레드로 안전하게 전파 */
    class MdcTaskDecorator : TaskDecorator {
        override fun decorate(runnable: Runnable): Runnable {
            val contextMap = MDC.getCopyOfContextMap()
            return Runnable {
                try {
                    if (contextMap != null) {
                        MDC.setContextMap(contextMap)
                    } else {
                        MDC.clear()
                    }
                    runnable.run()
                } finally {
                    MDC.clear()
                }
            }
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
