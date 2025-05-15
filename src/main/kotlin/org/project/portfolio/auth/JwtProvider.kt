package org.project.portfolio.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.project.portfolio.auth.domain.JwtUserDetails
import org.project.portfolio.auth.domain.RefreshToken
import org.project.portfolio.common.domain.exception.UnauthorizedException
import org.project.portfolio.user.domain.UserRole
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtProvider(
    private val jwtProperties: JwtProperties,
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    /**
     * JWT Access Token을 생성합니다.
     * @param email 사용자 이메일
     * @param roles 사용자 권한 목록
     * @param jit JWT ID (JIT)
     * @param issuedAt 발급 시간
     * @param duration 유효 시간 (초) 기본값: 1시간
     * @return JWT Access Token
     */
    fun createAccessToken(
        email: String,
        roles: Set<UserRole>,
        jit: String = UUID.randomUUID().toString(),
        issuedAt: Instant = Instant.now(),
        duration: Long = jwtProperties.accessExpiry,
    ): String {
        return createDefaultJwtBuilder(jit = jit, issuedAt = issuedAt, duration = duration)
            .subject(TokenType.ACCESS_TOKEN.name)
            .claim("email", email)
            .claim("roles", roles.map { it.authority })
            .compact()
    }

    /**
     * JWT Refresh Token을 생성합니다.
     * @param email 사용자 이메일
     * @param jit JWT ID (JIT)
     * @param issuedAt 발급 시간
     * @param duration 유효 시간 (초) 기본값: 1시간
     * @return JWT Refresh Token
     */
    fun createRefreshToken(
        email: String,
        jit: String,
        issuedAt: Instant = Instant.now(),
        duration: Long = jwtProperties.refreshExpiry,
    ): String {
        return createDefaultJwtBuilder(jit = jit, issuedAt = issuedAt, duration = duration)
            .subject(TokenType.REFRESH_TOKEN.name)
            .claim("email", email)
            .compact()
    }

    private fun createDefaultJwtBuilder(
        jit: String,
        issuedAt: Instant,
        duration: Long,
    ): JwtBuilder {
        return Jwts
            .builder()
            .header()
            .add("type", "JWT")
            .and()
            .id(jit)
            .issuer(jwtProperties.issuer)
            .notBefore(Date.from(issuedAt))
            .issuedAt(Date.from(issuedAt))
            .expiration(Date.from(issuedAt.plusSeconds(duration)))
            .signWith(key)
    }

    /**
     * JWT Access Token을 Decode합니다.
     * @throws UnauthorizedException JWT가 유효하지 않은 경우
     * @return JWT UserDetails
     */
    fun decodeAccessToken(token: String): UserDetails {
        val claims: Jws<Claims> = decode(token)

        val email: String = claims.payload["email"] as String
        val roles: Set<UserRole> = UserRole.convert(
            claims.payload["roles"] as List<String>,
        )

        return JwtUserDetails(email, roles)
    }

    /**
     * JWT Refresh Token을 Decode합니다.
     * @throws UnauthorizedException JWT가 유효하지 않은 경우
     * @return JWT RefreshToken
     */
    fun decodeRefreshToken(token: String): RefreshToken {
        val claims: Jws<Claims> = decode(token)

        val email: String = claims.payload["email"] as String
        val jit: String = claims.payload.id

        return RefreshToken(
            email = email,
            jit = jit,
        )
    }

    private fun decode(token: String): Jws<Claims> {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
        } catch (e: Exception) {
            throw UnauthorizedException("Invalid token")
        }
    }

    enum class TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN,
    }
}
