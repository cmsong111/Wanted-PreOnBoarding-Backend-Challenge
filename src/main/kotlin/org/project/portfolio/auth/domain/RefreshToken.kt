package org.project.portfolio.auth.domain

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.io.Serializable

@RedisHash(value = "refresh_token", timeToLive = 604800) // 7 days
data class RefreshToken(
    @Id
    val jit: String,
    val email: String,
) : Serializable
