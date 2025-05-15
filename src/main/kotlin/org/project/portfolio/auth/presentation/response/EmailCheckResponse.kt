package org.project.portfolio.auth.presentation.response

import io.swagger.v3.oas.annotations.media.Schema

data class EmailCheckResponse(
    @field:Schema(description = "이메일 사용 가능 여부")
    val available: Boolean,
)
