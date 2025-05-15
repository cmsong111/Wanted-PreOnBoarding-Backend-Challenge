package org.project.portfolio.user

import UsersFixture
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.auth.presentation.response.TokenResponse
import org.project.portfolio.user.domain.User
import org.project.portfolio.utils.prettyJson
import org.project.portfolio.utils.withJwt
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Transactional
@DirtiesContext
@DisplayName("User API 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserApiTest(
    private val mockMvc: MockMvc,
    private val authService: AuthService,
) : DescribeSpec(
    {
        extensions(SpringExtension)

        val userRegisterForm = UsersFixture.getRandomRegisterRequest()
        val user: User = authService.register(userRegisterForm)
        val userToken: TokenResponse = authService.login(userRegisterForm.email, userRegisterForm.password)

        describe("GET /api/v1/users/me - 내 정보 조회 API") {
            it("200 OK") {
                mockMvc.get("/api/v1/user") {
                    withJwt(userToken)
                }.andExpectAll {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.id") { value(user.id) }
                    jsonPath("$.email") { value(user.email) }
                }
            }

            it("401 Unauthorized") {
                mockMvc.get("/api/v1/user") {
                }.andExpectAll {
                    status { isUnauthorized() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                }
            }
        }

        describe("DELETE /api/v1/user - 회원 탈퇴 API") {
            it("200 OK") {
                // 회원 탈퇴
                mockMvc.delete("/api/v1/user") {
                    withJwt(userToken)
                }.andExpectAll {
                    status { isNoContent() }
                }

                // 회원 탈퇴 후, 해당 이메일로 회원가입 가능해야함
                mockMvc.post("/api/v1/auth/register/email") {
                    contentType = MediaType.APPLICATION_JSON
                    content = mapOf("email" to user.email).prettyJson()
                }.andExpectAll {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.available") { value(true) }
                }
            }

            it("401 Unauthorized") {
                mockMvc.delete("/api/v1/user") {
                }.andExpectAll {
                    status { isUnauthorized() }
                }
            }
        }
    },
)
