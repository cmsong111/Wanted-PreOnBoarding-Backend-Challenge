package org.project.portfolio.auth

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.project.portfolio.common.domain.exception.UnauthorizedException
import org.project.portfolio.config.JwtConfig
import org.project.portfolio.user.domain.UserRole
import org.springframework.security.oauth2.jwt.JwtValidationException
import java.time.Instant

class JwtProviderTest :
    DescribeSpec({
    val jwtProperties = JwtProperties(
        secret = "test-secret-key-at-least-32-bytes-long-for-hs256",
        issuer = "test-issuer",
        accessExpiry = 3600,
        refreshExpiry = 604800,
    )
    val jwtConfig = JwtConfig(jwtProperties)
    val accessDecoder = jwtConfig.jwtDecoder()
    val refreshDecoder = jwtConfig.refreshJwtDecoder()
    val authConverter = jwtConfig.jwtAuthenticationConverter()
    val jwtProvider = JwtProvider(
        jwtProperties = jwtProperties,
        jwtEncoder = jwtConfig.jwtEncoder(),
        refreshJwtDecoder = refreshDecoder,
    )

    describe("Spring OAuth2 Resource Server JWT") {
        it("Access Token을 발급하고 검증 및 권한 변환할 수 있다") {
            val token = jwtProvider.createAccessToken(
                email = "user@test.com",
                roles = setOf(UserRole.USER, UserRole.ADMIN),
            )

            val jwt = accessDecoder.decode(token)
            val auth = authConverter.convert(jwt)!!

            jwt.subject shouldBe "user@test.com"
            auth.name shouldBe "user@test.com"
            auth.authorities.map { it.authority } shouldContainExactlyInAnyOrder listOf("ROLE_USER", "ROLE_ADMIN")
        }

        it("Refresh Token을 발급하고 복호화할 수 있다") {
            val token = jwtProvider.createRefreshToken(
                email = "user@test.com",
                jit = "refresh-jit-123",
            )

            val decoded = jwtProvider.decodeRefreshToken(token)

            decoded.email shouldBe "user@test.com"
            decoded.jit shouldBe "refresh-jit-123"
        }

        it("Refresh Token을 Access Token 디코더로 검증하면 실패한다") {
            val refreshToken = jwtProvider.createRefreshToken(
                email = "user@test.com",
                jit = "refresh-jit-123",
            )

            shouldThrow<JwtValidationException> {
                accessDecoder.decode(refreshToken)
            }
        }

        it("Access Token을 Refresh Token으로 복호화하면 UnauthorizedException이 발생한다") {
            val accessToken = jwtProvider.createAccessToken(
                email = "user@test.com",
                roles = setOf(UserRole.USER),
            )

            shouldThrow<UnauthorizedException> {
                jwtProvider.decodeRefreshToken(accessToken)
            }
        }

        it("만료된 Access Token은 검증에 실패한다") {
            val expiredToken = jwtProvider.createAccessToken(
                email = "user@test.com",
                roles = setOf(UserRole.USER),
                issuedAt = Instant.now().minusSeconds(7200),
                duration = 60,
            )

            shouldThrow<JwtValidationException> {
                accessDecoder.decode(expiredToken)
            }
        }
    }
})
