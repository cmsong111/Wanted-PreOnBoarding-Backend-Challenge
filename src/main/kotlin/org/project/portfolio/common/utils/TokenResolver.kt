package org.project.portfolio.common.utils

import jakarta.servlet.http.HttpServletRequest

object TokenResolver {
    /**
     * 헤더 또는 쿠키에서 Bearer 토큰을 추출한다.
     * <p>우선적으로 헤더에서 추출하며, 헤더에 없을 경우 쿠키에서 추출한다.</p>
     * @param request HttpServletRequest
     * @return 토큰
     */
    fun resolveToken(request: HttpServletRequest): String? {
        // 헤더에서 토큰 추출
        val tokenFromHeader: String? = request.getHeader(AUTHORIZATION_HEADER)
        if (tokenFromHeader != null && tokenFromHeader.startsWith(BEARER_PREFIX)) {
            return tokenFromHeader.substring(BEARER_PREFIX.length)
        }
        return null
    }

    private const val AUTHORIZATION_HEADER = "Authorization"
    private const val BEARER_PREFIX = "Bearer "
}
