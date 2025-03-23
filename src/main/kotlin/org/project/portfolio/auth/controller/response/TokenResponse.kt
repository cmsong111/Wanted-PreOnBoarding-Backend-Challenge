package org.project.portfolio.auth.controller.response

import io.swagger.v3.oas.annotations.media.Schema

/** 토큰 응답 DTO */
@Schema(description = "토큰 응답")
data class TokenResponse(
    /** JWT Access Token */
    @field:Schema(
        description = "JWT Access Token",
        example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QHRlc3QuY29tIn0.anftQ7WoGLCy2PzchcnQy1wLi_EBoF9zSmsQBoRjoyQ",
    )
    val token: String,
)
