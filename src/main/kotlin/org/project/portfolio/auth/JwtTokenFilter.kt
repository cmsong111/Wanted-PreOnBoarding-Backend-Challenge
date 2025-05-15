package org.project.portfolio.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.project.portfolio.common.utils.TokenResolver
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.filter.OncePerRequestFilter

class JwtTokenFilter(
    private val tokenProvider: JwtProvider,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        TokenResolver.resolveToken(request)?.let { token ->
            try {
                val authenticatedUser: UserDetails = tokenProvider.decodeAccessToken(token)
                SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
                    authenticatedUser,
                    token,
                    authenticatedUser.authorities,
                )
            } catch (e: Exception) {
                logger.error("JWT token validation failed", e)
                SecurityContextHolder.clearContext()
            }
        }
        filterChain.doFilter(request, response)
    }
}
