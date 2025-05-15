package org.project.portfolio.auth.presentation.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/** 회원가입 폼 */
@Schema(description = "회원가입 폼")
data class RegisterForm(
    /** 이메일 */
    @field:Schema(description = "이메일", example = "test12345@test.com")
    @field:Size(max = 254, message = "이메일은 254자 이하로 입력해주세요")
    @field:Pattern(
        regexp = "^[A-Za-z0-9._%+-]{1,64}@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "이메일 형식이 올바르지 않습니다",
    )
    val email: String,
    /** 전화번호 */
    @field:Pattern(
        regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}\$",
        message = "전화번호 형식이 올바르지 않습니다",
    )
    @field:Schema(description = "전화번호", example = "010-1234-5678")
    @field:NotNull(message = "전화번호를 입력해주세요")
    val phone: String,
    /** 이름 */
    @field:Schema(description = "이름", example = "홍길동")
    @field:NotBlank(message = "이름을 입력해주세요")
    @field:Pattern(
        regexp = "^[가-힣a-zA-Z]{2,10}\$",
        message = "이름은 한글 또는 영어로만 작성해주세요",
    )
    val name: String,
    /** 비밀번호 */
    @field:Schema(description = "비밀번호", example = "Password1234~!")
    @field:NotBlank(message = "비밀번호를 입력해주세요")
    @field:Size(min = 8, max = 72, message = "비밀번호는 8자 이상 72자 이하로 입력해주세요")
    @field:Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{5,}\$",
        message = "비밀번호는 대소문자, 숫자, 특수문자를 포함한 5자 이상이어야 합니다",
    )
    val password: String,
)
