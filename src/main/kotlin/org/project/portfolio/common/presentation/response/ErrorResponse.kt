package org.project.portfolio.common.presentation.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Error API 응답")
data class ErrorResponse(
    @field:Schema(description = "에러 코드")
    val code: String,
    @field:Schema(description = "에러 메시지")
    val message: String?,
    @field:Schema(description = "에러 속성")
    val properties: Map<String, Any?> = mapOf(),
)
