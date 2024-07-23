package org.project.portfolio.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.project.portfolio.exception_handler.ApiResponse
import org.project.portfolio.exception_handler.ErrorCode
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    private val objectMapper: ObjectMapper = ObjectMapper()

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        ApiResponse(
            resultCode = ErrorCode.TOKEN_NOT_FOUND.code,
            resultMessage = ErrorCode.TOKEN_NOT_FOUND.message
        ).let {
            response.contentType = "application/json"
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.characterEncoding = "UTF-8"
            response.writer.write(objectMapper.writeValueAsString(it))
        }
    }
}
