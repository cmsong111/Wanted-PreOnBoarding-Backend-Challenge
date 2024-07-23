package org.project.portfolio.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders.BASE64
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.exception_handler.ErrorCode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider {

    @Value("\${jwt.secret-key}")
    lateinit var secretKey: String

    @Value("\${jwt.expiration}")
    lateinit var expiration: Integer


    val key: SecretKey by lazy {
        Keys.hmacShaKeyFor(BASE64.decode(secretKey))
    }


    /** Token Create */
    fun createToken(subject: String): String {
        return Jwts.builder()
            .issuer("portfolio")
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + expiration.toLong()))
            .subject(subject)
            .signWith(key)
            .compact()
    }

    /** resolve Token From Request */
    fun resolveToken(request: HttpServletRequest?): String? {
        return request?.getHeader("Authorization")?.takeIf { it.startsWith("Bearer ") }?.substring(7)
    }

    /** validate Token */
    fun validateToken(token: String): Boolean {
        return try {
            extractAllClaims(token)
            true
        } catch (e: BusinessException) {
            false
        }
    }

    /** extract All Claims*/
    private fun extractAllClaims(token: String): Claims {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            throw BusinessException(ErrorCode.TOKEN_INVALID)
        }
    }

    /** Get Username(Subject) From Token */
    fun getUsername(token: String): String {
        return extractAllClaims(token).subject
    }
}
