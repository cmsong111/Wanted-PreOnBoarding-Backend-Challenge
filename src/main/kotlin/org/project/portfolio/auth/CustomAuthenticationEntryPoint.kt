package org.project.portfolio.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.project.portfolio.exception_handler.ErrorCode
import org.project.portfolio.exception_handler.dto.ApiResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint(
    private val jwtProvider: JwtProvider
) : AuthenticationEntryPoint {

    private val objectMapper: ObjectMapper = ObjectMapper()

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val token: String? = jwtProvider.resolveToken(request);

        if (token.isNullOrEmpty()) {
            ApiResponse(
                resultCode = ErrorCode.TOKEN_NOT_FOUND.code,
                resultMessage = ErrorCode.TOKEN_NOT_FOUND.message
            ).let {
                response.contentType = "application/json"
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.characterEncoding = "UTF-8"
                response.writer.write(objectMapper.writeValueAsString(it))
            }
        } else {
            ApiResponse(
                resultCode = ErrorCode.TOKEN_EXPIRED.code,
                resultMessage = ErrorCode.TOKEN_EXPIRED.message
            ).let {
                response.contentType = "application/json"
                response.status = HttpServletResponse.SC_UNAUTHORIZED
                response.characterEncoding = "UTF-8"
                response.writer.write(objectMapper.writeValueAsString(it))
            }
        }
    }
}
