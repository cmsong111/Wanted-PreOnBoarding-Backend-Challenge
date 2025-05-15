package org.project.portfolio.auth

import UsersFixture
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.auth.presentation.request.EmailCheckForm
import org.project.portfolio.auth.presentation.request.RegisterForm
import org.project.portfolio.auth.presentation.response.TokenResponse
import org.project.portfolio.common.utils.TokenResolver.BEARER_PREFIX
import org.project.portfolio.common.utils.TokenResolver.REFRESH_TOKEN_HEADER
import org.project.portfolio.user.domain.User
import org.project.portfolio.utils.prettyJson
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Transactional
@DirtiesContext
@DisplayName("Auth API 통합 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthApiTest(
    private val mockMvc: MockMvc,
    private val authService: AuthService,
) : DescribeSpec(
    {
        extensions(SpringExtension)

        val userRegisterForm = UsersFixture.getRandomRegisterRequest()
        val author: User = authService.register(userRegisterForm)
        var authorToken: TokenResponse = authService.login(userRegisterForm.email, userRegisterForm.password)

        describe("POST /api/v1/auth/register - 회원가입 API") {
            it("200 OK") {
                // given
                val registerRequest = UsersFixture.getRandomRegisterRequest()
                logger.info { "회원가입 요청 정보: ${registerRequest.prettyJson()}" }

                // when & then
                mockMvc.post("/api/v1/auth/register") {
                    contentType = MediaType.APPLICATION_JSON
                    content = registerRequest.prettyJson()
                }.andExpectAll {
                    status { isCreated() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { isNumber() }
                    jsonPath("$.email") { value(registerRequest.email) }
                    jsonPath("$.name") { value(registerRequest.name) }
                }
            }

            it("400 Bad Request (@Valid)") {
                // given
                val invalidRegisterForm = RegisterForm(
                    email = "invalid-email",
                    password = "invalid-password",
                    name = "invalid-name",
                    phone = "invalid-phone",
                )
                logger.info { "회원가입 요청 정보: ${invalidRegisterForm.prettyJson()}" }

                // when & then
                mockMvc.post("/api/v1/auth/register") {
                    contentType = MediaType.APPLICATION_JSON
                    content = invalidRegisterForm.prettyJson()
                }.andExpectAll {
                    status { isBadRequest() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }
            }

            it("409 Conflict (이미 존재하는 이메일)") {
                // when & then
                mockMvc.post("/api/v1/auth/register") {
                    contentType = MediaType.APPLICATION_JSON
                    content = userRegisterForm.prettyJson()
                }.andExpectAll {
                    status { isConflict() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }
            }
        }

        describe("POST /api/v1/auth/register/email - 이메일 중복 확인 API") {
            it("200 OK") {
                // given
                val emailCheckForm: EmailCheckForm = UsersFixture.getRandomEmailCheckRequest()
                logger.info { "이메일 중복 확인 요청 정보: ${emailCheckForm.prettyJson()}" }

                // when & then
                mockMvc.post("/api/v1/auth/register/email") {
                    contentType = MediaType.APPLICATION_JSON
                    content = emailCheckForm.prettyJson()
                }.andExpectAll {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.available") { value(true) }
                }
            }

            it("409 Conflict (이미 존재하는 이메일)") {
                // when & then
                mockMvc.post("/api/v1/auth/register/email") {
                    contentType = MediaType.APPLICATION_JSON
                    content = userRegisterForm.prettyJson()
                }.andExpectAll {
                    status { isConflict() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }
            }
        }

        describe("POST /api/v1/auth/login - 로그인 API") {
            it("200 OK") {
                // when & then
                mockMvc.post("/api/v1/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = jacksonObjectMapper().writeValueAsString(
                        mapOf(
                            "email" to userRegisterForm.email,
                            "password" to userRegisterForm.password,
                        ),
                    )
                }.andExpectAll {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.accessToken") { isNotEmpty() }
                    jsonPath("$.refreshToken") { isNotEmpty() }
                }
            }
            it("400 Bad Request - Wrong Email") {
                // when & then
                mockMvc.post("/api/v1/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapOf(
                        "email" to "wrong-email@test.com",
                        "password" to userRegisterForm.password,
                    ).prettyJson()
                }.andExpectAll {
                    status { isBadRequest() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }
            }
            it("400 Bad Request - Wrong Password)") {
                // when & then
                mockMvc.post("/api/v1/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapOf(
                        "email" to userRegisterForm.email,
                        "password" to "wrong-password",
                    ).prettyJson()
                }.andExpectAll {
                    status { isBadRequest() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }
            }
        }

        describe("POST /api/v1/auth/refresh - 액세스 토큰 재발급 API") {
            it("200 OK") {
                // given
                val token = authService.login(
                    email = userRegisterForm.email,
                    password = userRegisterForm.password,
                )

                // when & then
                mockMvc.post("/api/v1/auth/refresh") {
                    header(REFRESH_TOKEN_HEADER, "$BEARER_PREFIX ${token.refreshToken}")
                }.andExpectAll {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.accessToken") { isNotEmpty() }
                    jsonPath("$.refreshToken") { isNotEmpty() }
                }
            }

            it("401 Unauthorized - Invalid Token") {
                // when & then
                mockMvc.post("/api/v1/auth/refresh") {
                    header(REFRESH_TOKEN_HEADER, "invalid-token")
                }.andExpectAll {
                    status { isUnauthorized() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }
            }

            it("401 Unauthorized - 리프래쉬 토큰 재사용 시도") {
                // given
                val token = authService.login(
                    email = userRegisterForm.email,
                    password = userRegisterForm.password,
                )

                // when & then
                mockMvc.post("/api/v1/auth/refresh") {
                    header(REFRESH_TOKEN_HEADER, "$BEARER_PREFIX ${token.refreshToken}")
                }.andExpectAll {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }

                mockMvc.post("/api/v1/auth/refresh") {
                    header(REFRESH_TOKEN_HEADER, "$BEARER_PREFIX ${token.refreshToken}")
                }.andExpectAll {
                    status { isUnauthorized() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }
            }
        }
    },
) {
    companion object {
        val logger: KLogger = KotlinLogging.logger {}
    }
}
