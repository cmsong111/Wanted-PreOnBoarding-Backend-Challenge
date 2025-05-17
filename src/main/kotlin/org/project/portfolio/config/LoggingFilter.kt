package org.project.portfolio.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

class LoggingFilter : OncePerRequestFilter() {
    /**
     * HTTP 요청을 필터링하여 traceId와 requestId를 MDC에 설정합니다.
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        // 요청에서 traceId와 requestId를 가져옵니다.
        val traceId = UUID.randomUUID().toString()
        val requestId = request.getHeader(REQUEST_ID) ?: "unknown"

        // 요청의 URI와 메서드를 가져옵니다.
        val requestURI = request.requestURI
        val method = request.method
        val query: String? = request.queryString?.takeIf { it.isNotEmpty() }

        try {
            // MDC에 traceId와 requestId를 설정
            MDC.put(TRACE_ID, traceId)
            MDC.put(REQUEST_ID, requestId)

            // 요청의 URI와 메서드를 로그에 기록
            MDC.put("uri", requestURI)
            MDC.put("method", method)
            if (query != null) {
                MDC.put("query", query)
            }

            response.setHeader(TRACE_ID, traceId)

            // 요청을 필터 체인에 전달
            filterChain.doFilter(request, response)
        } finally {
            // 요청이 끝난 후 MDC 클리어
            MDC.clear()
        }
    }

    companion object {
        const val TRACE_ID = "TRACE_ID"
        const val REQUEST_ID = "requestId"
    }
}
