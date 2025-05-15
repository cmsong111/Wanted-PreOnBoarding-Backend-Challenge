package org.project.portfolio.utils

import org.project.portfolio.auth.presentation.response.TokenResponse
import org.project.portfolio.common.utils.TokenResolver.AUTHORIZATION_HEADER
import org.project.portfolio.common.utils.TokenResolver.BEARER_PREFIX
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder

fun MockHttpServletRequestBuilder.withJwt(token: String): MockHttpServletRequestBuilder {
    return this.header(
        AUTHORIZATION_HEADER,
        "$BEARER_PREFIX $token",
    )
}

fun MockHttpServletRequestBuilder.withJwt(token: TokenResponse): MockHttpServletRequestBuilder {
    return this.header(
        AUTHORIZATION_HEADER,
        "$BEARER_PREFIX ${token.accessToken}",
    )
}

fun MockHttpServletRequestDsl.withJwt(token: TokenResponse) {
    header(
        AUTHORIZATION_HEADER,
        "$BEARER_PREFIX ${token.accessToken}",
    )
}

fun MockHttpServletRequestDsl.withJwt(token: String) {
    header(
        AUTHORIZATION_HEADER,
        "$BEARER_PREFIX $token",
    )
}
