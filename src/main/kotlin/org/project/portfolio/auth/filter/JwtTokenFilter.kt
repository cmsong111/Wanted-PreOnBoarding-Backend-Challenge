package org.project.portfolio.auth.filter

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.project.portfolio.auth.AuthenticatedUser
import org.project.portfolio.auth.JwtProvider
import org.project.portfolio.common.utils.TokenResolver
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
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
            val authenticatedUser: AuthenticatedUser = try {
                tokenProvider.decode(token)
            } catch (e: Exception) {
                logger.error(e.message)
                return
            }
            val authentication = UsernamePasswordAuthenticationToken(authenticatedUser, token, authenticatedUser.roles)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}
