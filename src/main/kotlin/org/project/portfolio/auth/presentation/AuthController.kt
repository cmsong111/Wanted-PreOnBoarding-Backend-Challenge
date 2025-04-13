package org.project.portfolio.auth.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.project.portfolio.auth.presentation.request.EmailCheckRequest
import org.project.portfolio.auth.presentation.request.LoginRequest
import org.project.portfolio.auth.presentation.request.RegisterRequest
import org.project.portfolio.auth.presentation.response.TokenResponse
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.config.SwaggerConfig.Companion.AUTH_API_TAG
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/** Auth API 컨트롤러 */
@Tag(name = AUTH_API_TAG, description = "The authentication API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    /**
     * 로그인 API
     * @param loginRequest 로그인 요청 정보
     * @return JWT 토큰
     */
    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "Authenticate the user and return the JWT token")
    fun login(
        @Valid @RequestBody loginRequest: LoginRequest,
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(
            authService.login(
                email = loginRequest.email,
                password = loginRequest.password,
            ),
        )
    }

    /**
     * 회원가입 API
     * @param registerRequest 회원가입 요청 정보
     * @return JWT 토큰
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "회원가입 API", description = "Create a new user account")
    fun register(
        @Valid @RequestBody registerRequest: RegisterRequest,
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(authService.register(registerRequest))
    }

    @PostMapping("/email-check")
    @Operation(summary = "이메일 사용 가능 여부 확인 API")
    fun checkEmail(
        @Valid @RequestBody emailCheckRequest: EmailCheckRequest,
    ): ResponseEntity<EmailCheckResponse> {
        return ResponseEntity.ok(
            EmailCheckResponse(
                !authService.existsEmail(
                    email = emailCheckRequest.email,
                ),
            ),
        )
    }

    data class EmailCheckResponse(
        @field:Schema(description = "이메일 사용 가능 여부")
        val available: Boolean,
    )
}
