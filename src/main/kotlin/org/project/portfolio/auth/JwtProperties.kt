package org.project.portfolio.auth

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * JWT 관련 설정을 담고 있는 클래스입니다.
 * @param secret JWT 서명에 사용되는 비밀키
 * @param issuer JWT 발급자
 * @param accessExpiry 액세스 토큰 만료 시간 (초 단위, 기본값: 1시간)
 * @param refreshExpiry 리프레시 토큰 만료 시간 (초 단위, 기본값: 7일)
 */
@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val issuer: String,
    val accessExpiry: Long = 3600,
    val refreshExpiry: Long = 604800,
)
