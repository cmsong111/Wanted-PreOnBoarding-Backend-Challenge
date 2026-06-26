package org.project.portfolio.config

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.proc.SecurityContext
import org.project.portfolio.auth.JwtProperties
import org.project.portfolio.auth.JwtProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

/** Spring Security OAuth2 Resource Server JWT 설정 */
@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtConfig(
    private val jwtProperties: JwtProperties,
) {
    private val secretKey: SecretKey = SecretKeySpec(
        jwtProperties.secret.toByteArray(Charsets.UTF_8),
        "HmacSHA256",
    )

    @Bean
    fun jwtEncoder(): JwtEncoder {
        return NimbusJwtEncoder(ImmutableSecret<SecurityContext>(secretKey))
    }

    /** Access Token 전용 JwtDecoder (Spring Security Resource Server 기본 사용) */
    @Bean
    @Primary
    fun jwtDecoder(): JwtDecoder {
        return buildDecoder(JwtProvider.TokenType.ACCESS_TOKEN)
    }

    /** Refresh Token 전용 JwtDecoder */
    @Bean
    @Qualifier("refreshJwtDecoder")
    fun refreshJwtDecoder(): JwtDecoder {
        return buildDecoder(JwtProvider.TokenType.REFRESH_TOKEN)
    }

    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val authoritiesConverter = JwtGrantedAuthoritiesConverter().apply {
            setAuthoritiesClaimName("roles")
            setAuthorityPrefix("")
        }
        return JwtAuthenticationConverter().apply {
            setJwtGrantedAuthoritiesConverter(authoritiesConverter)
            setPrincipalClaimName("sub")
        }
    }

    private fun buildDecoder(expectedType: JwtProvider.TokenType): JwtDecoder {
        val decoder = NimbusJwtDecoder.withSecretKey(secretKey)
            .macAlgorithm(MacAlgorithm.HS256)
            .build()

        val defaultValidator = JwtValidators.createDefaultWithIssuer(jwtProperties.issuer)
        val tokenTypeValidator = OAuth2TokenValidator<Jwt> { jwt ->
            val tokenType = jwt.getClaimAsString("token_type")
            if (tokenType == expectedType.name) {
                OAuth2TokenValidatorResult.success()
            } else {
                OAuth2TokenValidatorResult.failure(
                    OAuth2Error("invalid_token", "Expected ${expectedType.name} token", null),
                )
            }
        }

        decoder.setJwtValidator(DelegatingOAuth2TokenValidator(defaultValidator, tokenTypeValidator))
        return decoder
    }
}
