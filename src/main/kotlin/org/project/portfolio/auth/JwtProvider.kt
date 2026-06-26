package org.project.portfolio.auth

import org.project.portfolio.auth.domain.RefreshToken
import org.project.portfolio.common.domain.exception.UnauthorizedException
import org.project.portfolio.user.domain.UserRole
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class JwtProvider(
    private val jwtProperties: JwtProperties,
    private val jwtEncoder: JwtEncoder,
    @Qualifier("refreshJwtDecoder")
    private val refreshJwtDecoder: JwtDecoder,
) {
    /**
     * JWT Access Token을 생성합니다.
     */
    fun createAccessToken(
        email: String,
        roles: Set<UserRole>,
        jit: String = UUID.randomUUID().toString(),
        issuedAt: Instant = Instant.now(),
        duration: Long = jwtProperties.accessExpiry,
    ): String {
        val claims = defaultClaims(
            subject = email,
            jit = jit,
            issuedAt = issuedAt,
            duration = duration,
            tokenType = TokenType.ACCESS_TOKEN,
        )
            .claim("roles", roles.map { it.authority })
            .build()

        return encode(claims)
    }

    /**
     * JWT Refresh Token을 생성합니다.
     */
    fun createRefreshToken(
        email: String,
        jit: String,
        issuedAt: Instant = Instant.now(),
        duration: Long = jwtProperties.refreshExpiry,
    ): String {
        val claims = defaultClaims(
            subject = email,
            jit = jit,
            issuedAt = issuedAt,
            duration = duration,
            tokenType = TokenType.REFRESH_TOKEN,
        ).build()

        return encode(claims)
    }

    /**
     * JWT Refresh Token을 Decode합니다.
     * @throws UnauthorizedException JWT가 유효하지 않거나 Refresh Token이 아닌 경우
     */
    fun decodeRefreshToken(token: String): RefreshToken {
        return try {
            val jwt = refreshJwtDecoder.decode(token)
            RefreshToken(
                email = jwt.subject,
                jit = jwt.id,
            )
        } catch (e: JwtException) {
            throw UnauthorizedException("Invalid token")
        }
    }

    private fun defaultClaims(
        subject: String,
        jit: String,
        issuedAt: Instant,
        duration: Long,
        tokenType: TokenType,
    ): JwtClaimsSet.Builder {
        return JwtClaimsSet.builder()
            .id(jit)
            .issuer(jwtProperties.issuer)
            .subject(subject)
            .issuedAt(issuedAt)
            .notBefore(issuedAt)
            .expiresAt(issuedAt.plusSeconds(duration))
            .claim("token_type", tokenType.name)
    }

    private fun encode(claims: JwtClaimsSet): String {
        val header = JwsHeader.with(MacAlgorithm.HS256)
            .type("JWT")
            .build()
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
    }

    enum class TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN,
    }
}
