package org.project.portfolio.auth.service

import org.project.portfolio.auth.JwtProvider
import org.project.portfolio.auth.dto.LoginRequest
import org.project.portfolio.auth.dto.RegisterRequest
import org.project.portfolio.auth.dto.TokenResponse
import org.project.portfolio.common.exception.BusinessException
import org.project.portfolio.common.exception.ErrorCode
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

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
)  {
    /**
     * 회원가입
     * @param registerRequest 회원가입 요청 폼
     * @return JWT 토큰
     */
    fun register(registerRequest: RegisterRequest): TokenResponse {
        // 중복 확인
        if (userRepository.existsById(registerRequest.email!!)) {
            throw BusinessException(ErrorCode.USER_ALREADY_EXISTS)
        }

        val user = User(
            email = registerRequest.email,
            name = registerRequest.name!!,
            phone = registerRequest.phone!!,
            password = passwordEncoder.encode(registerRequest.password!!),
        )
        userRepository.save(user)
        return TokenResponse(token = jwtProvider.createToken(user))
    }

    /**
     * 로그인
     * @param loginRequest 로그인 요청 폼
     * @return JWT 토큰
     */
    fun login(loginRequest: LoginRequest): TokenResponse {
        val user = userRepository.findById(loginRequest.email!!).orElseThrow {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        if (!passwordEncoder.matches(loginRequest.password, user.password)) {
            throw BusinessException(ErrorCode.USER_INVALID_PASSWORD)
        }
        return TokenResponse(token = jwtProvider.createToken(user))
    }
}
