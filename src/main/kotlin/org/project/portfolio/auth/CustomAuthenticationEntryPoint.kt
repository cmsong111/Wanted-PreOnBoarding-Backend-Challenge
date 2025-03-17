package org.project.portfolio.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.project.portfolio.common.exception.ErrorCode
import org.project.portfolio.common.exception.dto.ApiResponse
import org.project.portfolio.common.utils.TokenResolver
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        val token: String? = TokenResolver.resolveToken(request)

        if (token.isNullOrEmpty()) {
            ApiResponse(
                resultCode = ErrorCode.TOKEN_NOT_FOUND.code,
                resultMessage = ErrorCode.TOKEN_NOT_FOUND.message,
            ).let {
                response.contentType = "application/json"
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.characterEncoding = "UTF-8"
                response.writer.write(objectMapper.writeValueAsString(it))
            }
        } else {
            ApiResponse(
                resultCode = ErrorCode.TOKEN_EXPIRED.code,
                resultMessage = ErrorCode.TOKEN_EXPIRED.message,
            ).let {
                response.contentType = "application/json"
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.characterEncoding = "UTF-8"
                response.writer.write(objectMapper.writeValueAsString(it))
            }
        }
    }
}
