package org.project.portfolio.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

/**
 * JWT 관련 설정 (기동 시점 Fail-Fast 검증)
 * @param secret JWT 서명에 사용되는 비밀키 (HS256 최소 32바이트)
 * @param issuer JWT 발급자
 * @param accessExpiry 액세스 토큰 만료 시간 (초 단위, 기본값: 1시간)
 * @param refreshExpiry 리프레시 토큰 만료 시간 (초 단위, 기본값: 7일)
 */
@Validated
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    @field:NotBlank(message = "JWT secret은 비어있을 수 없습니다")
    @field:Size(min = 32, message = "HS256 서명을 위해 JWT secret은 최소 32자 이상이어야 합니다")
    val secret: String,
    @field:NotBlank(message = "JWT issuer는 비어있을 수 없습니다")
    val issuer: String,
    @field:Positive(message = "accessExpiry는 양수여야 합니다")
    val accessExpiry: Long = 3600,
    @field:Positive(message = "refreshExpiry는 양수여야 합니다")
    val refreshExpiry: Long = 604800,
)
