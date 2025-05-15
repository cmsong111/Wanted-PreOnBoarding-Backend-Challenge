package org.project.portfolio.auth.application

import io.github.oshai.kotlinlogging.KotlinLogging
import org.project.portfolio.auth.JwtProvider
import org.project.portfolio.auth.domain.RefreshToken
import org.project.portfolio.auth.presentation.request.RegisterForm
import org.project.portfolio.auth.presentation.response.TokenResponse
import org.project.portfolio.common.domain.exception.BadRequestException
import org.project.portfolio.common.domain.exception.ConflictException
import org.project.portfolio.common.domain.exception.UnauthorizedException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

/**
 * 사용자 인증 서비스
 * @param userRepository 사용자 레포지토리
 * @param jwtProvider JWT 토큰 생성 및 검증
 * @param passwordEncoder 비밀번호 암호화 및 검증
 */
@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRedisRepository,
    private val jwtProvider: JwtProvider,
    private val passwordEncoder: PasswordEncoder,
) {
    /**
     * 회원가입
     * @param registerForm 회원가입 요청 폼
     * @return JWT 토큰
     */
    @Transactional
    fun register(registerForm: RegisterForm): User {
        // 중복 확인
        if (userRepository.existsByEmail(registerForm.email)) {
            throw ConflictException("이미 존재하는 이메일입니다.")
        }

        return userRepository.save(
            User.create(
                email = registerForm.email,
                name = registerForm.name,
                phone = registerForm.phone,
                password = passwordEncoder.encode(registerForm.password),
            ),
        )
    }

    /**
     * 로그인
     * @param email 로그인 요청 폼
     * @param password 비밀번호
     * @return JWT 토큰
     */
    @Transactional
    fun login(
        email: String,
        password: String,
    ): TokenResponse {
        // 유저 조회
        val user: User = userRepository.findByEmail(email) ?: throw BadRequestException(
            message = "이메일 또는 비밀번호가 잘못되었습니다.",
        )

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.password)) {
            throw BadRequestException(
                message = "이메일 또는 비밀번호가 잘못되었습니다.",
            )
        }

        // Refresh Token 발급
        val refreshToken: String = UUID.randomUUID().toString()
        refreshTokenRepository.save(RefreshToken(jit = refreshToken, email = user.email))

        return TokenResponse(
            accessToken = jwtProvider.createAccessToken(email = user.email, roles = user.roles),
            refreshToken = jwtProvider.createRefreshToken(email = user.email, jit = refreshToken),
        )
    }

    /**
     * 엑세스 토큰 재발급
     * @param token 리프레시 토큰 (ex. eyj...)
     * @return
     */
    @Transactional
    fun reissueAccessToken(token: String): TokenResponse {
        // 리프레시 토큰 파싱
        val refreshToken: RefreshToken = jwtProvider.decodeRefreshToken(token)

        // 리프레시 토큰 검증
        if (!refreshTokenRepository.existsById(refreshToken.jit)) {
            throw UnauthorizedException("리프레시 토큰이 존재하지 않습니다.")
        }

        // 유저 조회
        val user: User = userRepository.findByEmail(refreshToken.email) ?: throw UnauthorizedException("유저가 존재하지 않습니다.")

        // 기존 리프레시 토큰 삭제
        refreshTokenRepository.deleteById(refreshToken.jit)

        // 리프레시 토큰 재발급
        val newRefreshToken: String = UUID.randomUUID().toString()
        refreshTokenRepository.save(RefreshToken(jit = newRefreshToken, email = user.email))

        // 엑세스 토큰 재발급
        return TokenResponse(
            accessToken = jwtProvider.createAccessToken(email = user.email, roles = user.roles),
            refreshToken = jwtProvider.createRefreshToken(email = user.email, jit = newRefreshToken),
        )
    }

    /**
     * 이메일 중복 확인
     * @param email 이메일
     * @return 중복 여부 (true: 중복, false: 중복 아님)
     */
    @Transactional(readOnly = true)
    fun existsEmail(email: String): Boolean {
        if (userRepository.existsByEmail(email)) {
            throw ConflictException(
                message = "이미 존재하는 이메일입니다.",
                properties = mapOf("email" to email),
            )
        }
        return false
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
