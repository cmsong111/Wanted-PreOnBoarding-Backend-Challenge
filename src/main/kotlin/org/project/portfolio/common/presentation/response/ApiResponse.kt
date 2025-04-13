package org.project.portfolio.common.presentation.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "API 응답")
data class ApiResponse<T>(
    @field:Schema(description = "요청 성공 여부")
    val success: Boolean = true,
    @field:Schema(description = "응답 데이터")
    val data: T? = null,
    @field:Schema(description = "에러 정보")
    val error: Error?,
) {
    companion object {
        fun <T> success(data: T?): ApiResponse<T> {
            return ApiResponse(
                success = true,
                data = data,
                error = null,
            )
        }

        fun <T> fail(
            code: String,
            message: String? = null,
            properties: Map<String, Any?> = mapOf(),
        ): ApiResponse<T> {
            return ApiResponse(
                success = false,
                data = null,
                error =
                    Error(
                        code = code,
                        message = message,
                        properties = properties,
                    ),
            )
        }
    }
}

data class Error(
    val code: String,
    val message: String?,
    val properties: Map<String, Any?> = mapOf(),
)
