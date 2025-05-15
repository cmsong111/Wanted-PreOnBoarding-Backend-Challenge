package org.project.portfolio.auth.presentation.response

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 토큰 응답 DTO
 * @param accessToken JWT Access Token
 * @param refreshToken JWT Refresh Token
 * @author Namju Kim
 */
@Schema(description = "토큰 응답")
data class TokenResponse(
    @field:Schema(
        description = "JWT Access Token",
        example = "eyJ0eXBlIjoiSldUIn0=...",
    )
    val accessToken: String,
    @field:Schema(
        description = "JWT Refresh Token",
        example = "eyJ0eXBlIjoiSldUIn0=...",
    )
    val refreshToken: String,
)
