package org.project.portfolio.user.presentation.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

@Schema(description = "비밀번호 변경 요청")
data class PasswordChangeRequest(
    @field:Schema(description = "현재 비밀번호", example = "Password1234~!")
    @field:NotBlank(message = "현재 비밀번호를 입력해주세요")
    val currentPassword: String,
    @field:Schema(description = "새 비밀번호", example = "NewPassword1234~!")
    @field:NotBlank(message = "새 비밀번호를 입력해주세요")
    @field:Size(min = 8, max = 72, message = "비밀번호는 8자 이상 72자 이하로 입력해주세요")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{5,}\$",
        message = "비밀번호는 대소문자, 숫자, 특수문자를 포함한 5자 이상이어야 합니다",
    )
    val newPassword: String,
)
