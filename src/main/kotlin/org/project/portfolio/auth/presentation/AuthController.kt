package org.project.portfolio.auth.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.auth.presentation.request.EmailCheckForm
import org.project.portfolio.auth.presentation.request.LoginForm
import org.project.portfolio.auth.presentation.request.RegisterForm
import org.project.portfolio.auth.presentation.response.EmailCheckResponse
import org.project.portfolio.auth.presentation.response.TokenResponse
import org.project.portfolio.common.utils.TokenResolver
import org.project.portfolio.common.utils.TokenResolver.REFRESH_TOKEN_HEADER
import org.project.portfolio.config.SwaggerConfig.Companion.AUTH_API_TAG
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.presentation.response.UserSummaryResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/** Auth API 컨트롤러 */
@Tag(name = AUTH_API_TAG, description = "Auth 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    /**
     * 로그인 API
     * @param loginForm 로그인 요청 정보
     * @return JWT 토큰
     */
    @PostMapping("/login")
    @Operation(
        summary = "로그인 API",
        description = """## 로그인 API
- 회원가입된 사용자만 로그인 가능
- 로그인 성공 시 엑세스 토큰과 리프레시 토큰을 발급
- 로그인 실패 시 400 에러 반환
  - 어떤 이유로 실패했는지 반환하지 않음 (보안상 이유)
## 토큰 정보
- 엑세스 토큰
    - 1시간 유효
    - 엑세스 토큰 만료 시 401이 반환
- 리프레시 토큰
    - 7일간 유효
    - 1회용이므로 재사용 불가
""",
    )
    fun login(
        @Valid @RequestBody loginForm: LoginForm,
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(
            authService.login(
                email = loginForm.email,
                password = loginForm.password,
            ),
        )
    }

    @PostMapping("/refresh")
    @Operation(
        summary = "리프래쉬 토큰을 이용한 엑세스 토큰 재발급 API",
        description = """## 리프래쉬 토큰을 이용한 엑세스 토큰 재발급 API
- Refresh-Token 헤더에 리프래쉬 토큰을 담아 요청
- 리프래쉬 토큰이 유효하지 않거나 만료된 경우 401 에러 반환
- 리프래쉬 토큰은 1회용이므로, 반환되는 엑세스 토큰과 리프래쉬 토큰을 저장해야함
""",
    )
    fun refresh(
        @Parameter(description = "리프래쉬 토큰", example = "Bearer eyJ0eXBlIjoiSldUIn0=")
        @RequestHeader(REFRESH_TOKEN_HEADER) rawToken: String,
    ): ResponseEntity<TokenResponse> {
        return ResponseEntity.ok(
            authService.reissueAccessToken(
                token = TokenResolver.resolveToken(rawToken),
            ),
        )
    }

    /**
     * 회원가입 API
     * @param registerForm 회원가입 요청 정보
     * @return JWT 토큰
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "회원가입 API",
        description = """""",
    )
    fun register(
        @Valid @RequestBody registerForm: RegisterForm,
    ): ResponseEntity<UserSummaryResponse> {
        // 회원가입
        val user: User = authService.register(
            registerForm = registerForm,
        )
        return ResponseEntity
            .created(URI.create("/api/v1/users/${user.id}"))
            .body(UserSummaryResponse.from(user))
    }

    @PostMapping("/register/email")
    @Operation(
        summary = "이메일 사용 가능 여부 확인 API",
        description = """## 이메일 사용 가능 여부 확인 API
- 사용 가능한 이메일인 경우 200 OK 반환
- 사용 불가능한 이메일인 경우 409 Conflict 반환
""",
    )
    fun checkEmail(
        @Valid @RequestBody emailCheckForm: EmailCheckForm,
    ): ResponseEntity<EmailCheckResponse> {
        return ResponseEntity.ok(
            EmailCheckResponse(
                !authService.existsEmail(
                    email = emailCheckForm.email,
                ),
            ),
        )
    }
}
