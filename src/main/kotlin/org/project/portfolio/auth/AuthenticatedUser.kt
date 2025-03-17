package org.project.portfolio.auth

import org.project.portfolio.user.entity.UserRole
import java.time.Instant

/**
 * JWT Payload에 담길 사용자 정보
 */
data class AuthenticatedUser(
    /** JWT ID */
    val jti: String,
    /** 사용자 이메일 */
    val email: String,
    /** 사용자 역할 */
    val roles: Set<UserRole>,
    /** 발급자 */
    val issuer: String,
    /** 발급 시간 */
    val issuedAt: Instant,
    /** 만료 시간 */
    val expiry: Instant,
)
