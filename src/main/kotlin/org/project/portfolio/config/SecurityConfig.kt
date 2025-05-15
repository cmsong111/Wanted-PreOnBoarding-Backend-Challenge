package org.project.portfolio.config

import org.project.portfolio.auth.JwtProvider
import org.project.portfolio.auth.JwtTokenFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter

/** 스프링 시큐리티 설정 */
@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val jwtProvider: JwtProvider,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .headers { it.disable() }
            .sessionManagement { it.disable() }
            .authorizeHttpRequests { request ->
                request.anyRequest().permitAll()
            }
            .addFilterBefore(
                JwtTokenFilter(jwtProvider),
                BasicAuthenticationFilter::class.java,
            )
            .build()
    }
}
