package org.project.portfolio.auth.presentation.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email

@Schema(description = "이메일 중복 확인 폼")
data class EmailCheckRequest(
    @field:Email(message = "이메일 형식이 아닙니다")
    val email: String,
)
