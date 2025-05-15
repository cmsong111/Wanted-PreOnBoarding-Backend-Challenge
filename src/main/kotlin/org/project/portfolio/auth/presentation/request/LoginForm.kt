package org.project.portfolio.auth.presentation.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern

/** 로그인 폼 */
@Schema(description = "로그인 폼")
data class LoginForm(
    /** 이메일 */
    @field:Schema(description = "이메일", example = "test12345@test.com")
    @field:Pattern(
        regexp = "^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "이메일 형식이 아닙니다",
    )
    val email: String = "",
    /** 비밀번호 */
    @field:Schema(description = "비밀번호", example = "Password1234~!")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{5,}\$",
        message = "비밀번호는 대소문자, 숫자, 특수문자를 포함한 5자 이상이어야 합니다",
    )
    val password: String,
)
