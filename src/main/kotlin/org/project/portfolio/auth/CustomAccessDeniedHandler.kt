package org.project.portfolio.auth

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.project.portfolio.exception_handler.ErrorCode
import org.project.portfolio.exception_handler.dto.ApiResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler : AccessDeniedHandler {

    private val objectMapper: ObjectMapper = ObjectMapper()

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        ApiResponse(
            resultCode = ErrorCode.INVALID_ACCESS.code,
            resultMessage = ErrorCode.INVALID_ACCESS.message
        ).let {
            response.contentType = "application/json"
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.characterEncoding = "UTF-8"
            response.writer.write(objectMapper.writeValueAsString(it))
        }
    }
}

