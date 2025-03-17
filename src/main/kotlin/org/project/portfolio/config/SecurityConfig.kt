package org.project.portfolio.config

import org.project.portfolio.auth.CustomAccessDeniedHandler
import org.project.portfolio.auth.CustomAuthenticationEntryPoint
import org.project.portfolio.auth.JwtProvider
import org.project.portfolio.auth.filter.JwtTokenFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/** 스프링 시큐리티 설정 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val userDetailsService: UserDetailsService,
    private val jwtProvider: JwtProvider,
    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .headers { it.disable() }
            .sessionManagement { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers(*WHITE_LIST_URL).permitAll()
                    // 게시글 조회(GET) API는 인증 없이 접근 가능
                    .requestMatchers(HttpMethod.GET, "/api/v1/article/**").permitAll()
                    .anyRequest().authenticated()
            }
            .userDetailsService(userDetailsService)
            .addFilterBefore(JwtTokenFilter(jwtProvider), UsernamePasswordAuthenticationFilter::class.java)
            .exceptionHandling {
                it.authenticationEntryPoint(customAuthenticationEntryPoint)
                it.accessDeniedHandler(customAccessDeniedHandler)
            }
            .build()
    }

    companion object {
        /** 허용 URL 목록 */
        private val WHITE_LIST_URL = arrayOf<String>(
            "/api/v1/auth/**",
            "/h2-console/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/",
        )
    }
}
