package org.project.portfolio.config

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.project.portfolio.common.presentation.response.ErrorResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/** 스프링 시큐리티 설정 */
@Configuration
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationConverter: JwtAuthenticationConverter,
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .headers { headers -> headers.frameOptions { it.sameOrigin() } }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    .requestMatchers(
                        "/api/v1/auth/login",
                        "/api/v1/auth/register",
                        "/api/v1/auth/register/email",
                        "/api/v1/auth/refresh",
                        "/api/v1/auth/logout",
                    ).permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/articles", "/api/v1/articles/*", "/api/v1/articles/*/comments").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/v1/users/*").permitAll()
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/actuator/health",
                    ).permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2.jwt { it.jwtAuthenticationConverter(jwtAuthenticationConverter) }
                oauth2.authenticationEntryPoint(jsonAuthenticationEntryPoint())
                oauth2.accessDeniedHandler(jsonAccessDeniedHandler())
            }
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(jsonAuthenticationEntryPoint())
                exceptions.accessDeniedHandler(jsonAccessDeniedHandler())
            }
            .addFilterBefore(
                LoggingFilter(),
                UsernamePasswordAuthenticationFilter::class.java,
            )
            .build()
    }

    private fun jsonAuthenticationEntryPoint(): AuthenticationEntryPoint =
        AuthenticationEntryPoint { _, response, _ ->
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            objectMapper.writeValue(
                response.outputStream,
                ErrorResponse(
                    code = "UNAUTHORIZED",
                    message = "인증이 필요합니다.",
                ),
            )
        }

    private fun jsonAccessDeniedHandler(): AccessDeniedHandler =
        AccessDeniedHandler { _, response, _ ->
            response.status = HttpServletResponse.SC_FORBIDDEN
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            objectMapper.writeValue(
                response.outputStream,
                ErrorResponse(
                    code = "FORBIDDEN",
                    message = "접근 권한이 없습니다.",
                ),
            )
        }
}
