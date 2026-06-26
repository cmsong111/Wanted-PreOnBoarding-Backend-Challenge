package org.project.portfolio.notification

import UsersFixture
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import org.project.portfolio.auth.application.AuthService
import org.project.portfolio.notification.application.NotificationCommandService
import org.project.portfolio.notification.presentation.request.NotificationRequestDto
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.user.domain.UserRole
import org.project.portfolio.utils.prettyJson
import org.project.portfolio.utils.withJwt
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@Transactional
@DisplayName("Notifications API 통합 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class NotificationsApiTest(
    private val mockMvc: MockMvc,
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val notificationCommandService: NotificationCommandService,
) : DescribeSpec(
    {
        val userForm = UsersFixture.getRandomRegisterRequest()
        val user: User = authService.register(userForm)
        val userToken = authService.login(userForm.email, userForm.password)

        val adminForm = UsersFixture.getRandomRegisterRequest()
        val admin: User = authService.register(adminForm).apply {
            addRole(UserRole.ADMIN)
            userRepository.save(this)
        }
        val adminToken = authService.login(adminForm.email, adminForm.password)

        describe("GET /api/v1/notifications - 알림 내역 조회 API") {
            it("200 OK - DTO로 반환되며 receiver 비밀번호 등 민감정보가 노출되지 않는다") {
                notificationCommandService.sendNotification(
                    NotificationRequestDto(
                        title = "테스트 알림",
                        content = "알림 본문입니다.",
                        receiver = user.id,
                        sender = "system",
                    ),
                    sender = "system",
                )

                mockMvc.get("/api/v1/notifications") {
                    withJwt(userToken)
                }.andExpectAll {
                    status { isOk() }
                    content { contentType(MediaType.APPLICATION_JSON) }
                    jsonPath("$.content[0].title") { value("테스트 알림") }
                    jsonPath("$.content[0].receiverId") { value(user.id) }
                    jsonPath("$.content[0].receiver") { doesNotExist() }
                }
            }

            it("401 Unauthorized - 비인증 요청 차단") {
                mockMvc.get("/api/v1/notifications")
                    .andExpectAll {
                        status { isUnauthorized() }
                    }
            }
        }

        describe("GET /api/v1/notifications/subscribe - SSE 구독 API") {
            it("401 Unauthorized - 비인증 구독 요청 차단") {
                mockMvc.get("/api/v1/notifications/subscribe")
                    .andExpectAll {
                        status { isUnauthorized() }
                    }
            }
        }

        describe("POST /api/v1/notifications - 관리자 알림 발송 API") {
            it("202 Accepted - 관리자는 알림을 발송할 수 있다") {
                val request = NotificationRequestDto(
                    title = "공지사항",
                    content = "점검 안내입니다.",
                    receiver = user.id,
                    sender = admin.email,
                )

                mockMvc.post("/api/v1/notifications") {
                    withJwt(adminToken)
                    contentType = MediaType.APPLICATION_JSON
                    content = request.prettyJson()
                }.andExpectAll {
                    status { isAccepted() }
                }
            }

            it("403 Forbidden - 일반 사용자는 알림을 발송할 수 없다") {
                val request = NotificationRequestDto(
                    title = "공지사항",
                    content = "점검 안내입니다.",
                    receiver = user.id,
                    sender = user.email,
                )

                mockMvc.post("/api/v1/notifications") {
                    withJwt(userToken)
                    contentType = MediaType.APPLICATION_JSON
                    content = request.prettyJson()
                }.andExpectAll {
                    status { isForbidden() }
                }
            }
        }
    },
)
