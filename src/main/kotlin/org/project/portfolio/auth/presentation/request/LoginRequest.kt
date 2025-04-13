package org.project.portfolio.auth.presentation.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/** 로그인 폼 */
@Schema(description = "로그인 폼")
data class LoginRequest(
    /** 이메일 */
    @field:Schema(description = "이메일", example = "test@test.com")
    @field:Email(message = "이메일 형식이 아닙니다")
    val email: String = "",
    /** 비밀번호 */
    @field:Schema(description = "비밀번호", example = "Password1234~!")
    @field:NotBlank(message = "비밀번호를 입력해주세요")
    val password: String,
)
