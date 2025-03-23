package org.project.portfolio.auth.service

import org.project.portfolio.auth.JwtProvider
import org.project.portfolio.auth.controller.request.RegisterRequest
import org.project.portfolio.auth.controller.response.TokenResponse
import org.project.portfolio.common.exception.BusinessException
import org.project.portfolio.common.exception.ErrorCode
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 사용자 인증 서비스
 * @param userRepository 사용자 레포지토리
 * @param jwtProvider JWT 토큰 생성 및 검증
 * @param passwordEncoder 비밀번호 암호화 및 검증
 */
@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtProvider: JwtProvider,
    private val passwordEncoder: PasswordEncoder,
) {
    /**
     * 회원가입
     * @param registerRequest 회원가입 요청 폼
     * @return JWT 토큰
     */
    fun register(registerRequest: RegisterRequest): TokenResponse {
        // 중복 확인
        if (existsEmail(registerRequest.email)) {
            throw BusinessException(ErrorCode.USER_ALREADY_EXISTS)
        }

        val user = User(
            email = registerRequest.email,
            name = registerRequest.name,
            phone = registerRequest.phone,
            password = passwordEncoder.encode(registerRequest.password),
        )
        userRepository.save(user)
        return TokenResponse(token = jwtProvider.createToken(user))
    }

    /**
     * 로그인
     * @param loginRequest 로그인 요청 폼
     * @return JWT 토큰
     */
    fun login(
        email: String,
        password: String,
    ): TokenResponse {
        // 유저 조회
        val user = userRepository.findByEmail(email)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.password)) {
            throw BusinessException(ErrorCode.USER_INVALID_PASSWORD)
        }
        // 로그인 성공 및 토큰 발급
        return TokenResponse(
            token = jwtProvider.createToken(user),
        )
    }

    /**
     * 이메일 중복 확인
     * @param email 이메일
     * @return 중복 여부 (true: 중복, false: 중복 아님)
     */
    @Transactional(readOnly = true)
    fun existsEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }
}
