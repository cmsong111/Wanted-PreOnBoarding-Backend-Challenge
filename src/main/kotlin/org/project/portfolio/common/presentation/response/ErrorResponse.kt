package org.project.portfolio.common.presentation.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.config.LoggingFilter
import org.slf4j.MDC
import java.time.Instant

@Schema(description = "Error API 응답")
data class ErrorResponse(
    @field:Schema(description = "에러 코드")
    val code: String,
    @field:Schema(description = "에러 메시지")
    val message: String?,
    @field:Schema(description = "에러 속성")
    val properties: Map<String, Any?> = mapOf(),
    @field:Schema(description = "요청 추적 ID")
    val traceId: String? = MDC.get(LoggingFilter.TRACE_ID),
    @field:Schema(description = "에러 발생 시각")
    val timestamp: Instant = Instant.now(),
)
