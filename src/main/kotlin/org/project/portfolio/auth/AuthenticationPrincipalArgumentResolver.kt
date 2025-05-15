package org.project.portfolio.auth

import org.project.portfolio.common.domain.exception.UnauthorizedException
import org.springframework.core.MethodParameter
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class AuthenticationPrincipalArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(AuthenticationPrincipal::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth.principal == "anonymousUser") {
            throw UnauthorizedException("인증되지 않은 사용자입니다.")
        }
        return auth.principal
    }
}
