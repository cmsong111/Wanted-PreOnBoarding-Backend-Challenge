package org.project.portfolio.auth.presentation.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "이메일 중복 확인 폼")
data class EmailCheckForm(
    @field:Schema(description = "이메일", example = "test12345@test.com")
    @field:Size(max = 254, message = "이메일은 254자 이하로 입력해주세요")
    @field:Pattern(
        regexp = "^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "이메일 형식이 올바르지 않습니다",
    )
    val email: String,
)
