package org.project.portfolio.auth

import UsersFixture
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.junit.jupiter.api.DisplayName
import org.project.portfolio.auth.presentation.request.RegisterRequest
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.utils.prettyJson
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional

@Transactional
@DirtiesContext
@DisplayName("Articles API 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuthApiTest(
    private val mockMvc: MockMvc,
    private val authService: AuthService,
) : DescribeSpec(
    {
        extensions(SpringExtension)

        describe("POST /api/v1/auth/register - 회원가입 API") {
            it("200 OK") {
                // given
                val registerRequest = UsersFixture.getRandomRegisterRequest().let {
                    logger.info { "회원가입 요청 정보: ${it.prettyJson()}" }
                    it
                }

                // when & then
                mockMvc.post("/api/v1/auth/register") {
                    contentType = MediaType.APPLICATION_JSON
                    content = jacksonObjectMapper().writeValueAsString(registerRequest)
                }.andExpect {
                    status().isCreated
                    content().contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.token").isNotEmpty
                }
            }

            it("400 Bad Request (@Valid)") {
                // given
                val invalidRegisterRequest = RegisterRequest(
                    email = "invalid-email",
                    password = "invalid-password",
                    name = "invalid-name",
                    phone = "invalid-phone",
                )
                logger.info { "회원가입 요청 정보: ${invalidRegisterRequest.prettyJson()}" }

                // when & then
                mockMvc.post("/api/v1/auth/register") {
                    contentType = MediaType.APPLICATION_JSON
                    content = jacksonObjectMapper().writeValueAsString(invalidRegisterRequest)
                }.andExpect {
                    status().isBadRequest
                    content().contentType(MediaType.APPLICATION_JSON)
                }
            }

            it("409 Conflict (이미 존재하는 이메일)") {
                // given
                val alreadyRegisteredUser = UsersFixture.getRandomRegisterRequest().let {
                    logger.info { "회원가입 요청 정보: ${it.prettyJson()}" }
                    authService.register(it)
                    it
                }

                // when & then
                mockMvc.post("/api/v1/auth/register") {
                    contentType = MediaType.APPLICATION_JSON
                    content = jacksonObjectMapper().writeValueAsString(alreadyRegisteredUser)
                }.andExpect {
                    status().isConflict
                    content().contentType(MediaType.APPLICATION_JSON)
                }
            }
        }

        describe("POST /api/v1/auth/login - 로그인 API") {
            it("200 OK") {
                // given
                val alreadyRegisteredUser = UsersFixture.getRandomRegisterRequest().let {
                    logger.info { "회원가입 요청 정보: ${it.prettyJson()}" }
                    authService.register(it)
                    it
                }

                // when & then
                mockMvc.post("/api/v1/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = jacksonObjectMapper().writeValueAsString(
                        mapOf(
                            "email" to alreadyRegisteredUser.email,
                            "password" to alreadyRegisteredUser.password,
                        ),
                    )
                }.andExpect {
                    status().isOk
                    content().contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.token").isNotEmpty
                }
            }

            it("400 Bad Request (잘못된 요청)") {
                // given
                val alreadyRegisteredUser: RegisterRequest = UsersFixture.getRandomRegisterRequest().let {
                    logger.info { "회원가입 정보: ${it.prettyJson()}" }
                    authService.register(it)
                    it
                }
                val invalidLoginRequest = UsersFixture.getRandomRegisterRequest().let {
                    logger.info { "잘못된 로그인 요청 정보: ${it.prettyJson()}" }
                    authService.register(it)
                    it
                }

                // when & then
                mockMvc.post("/api/v1/auth/login") {
                    contentType = MediaType.APPLICATION_JSON
                    content = jacksonObjectMapper().writeValueAsString(
                        mapOf(
                            "email" to alreadyRegisteredUser.email,
                            "password" to invalidLoginRequest.password,
                        ),
                    )
                }.andExpect {
                    status().isBadRequest
                    content().contentType(MediaType.APPLICATION_JSON)
                }
            }
        }
    },
) {
    companion object {
        val logger: KLogger = KotlinLogging.logger {}
    }
}
