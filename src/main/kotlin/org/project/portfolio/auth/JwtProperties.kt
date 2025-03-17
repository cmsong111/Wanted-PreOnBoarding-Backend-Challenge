package org.project.portfolio.auth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val issuer: String,
    val expiry: Long,
) {
    init {
        println("Secret: $secret")
        println("Issuer: $issuer")
        println("Expiry: $expiry")
    }
}
