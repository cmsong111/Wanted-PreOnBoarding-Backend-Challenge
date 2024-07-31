package org.project.portfolio.notification.service

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.project.portfolio.notification.dto.NotificationRequestDto
import org.project.portfolio.notification.entity.Notification
import org.project.portfolio.notification.repository.NotificationRepository
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.data.domain.PageRequest
import kotlin.test.assertNotNull

@DisplayName("NotificationService 단위 테스트")
@ExtendWith(MockitoExtension::class)
class NotificationServiceTest {

    @InjectMocks
    private lateinit var notificationService: NotificationService

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @Mock
    private lateinit var userRepository: UserRepository

    @Test
    fun notificationSubscribe() {
        val email: String = "test@test.com"
        val notifications: List<Notification> = listOf(
            Notification(
                title = "알림 제목",
                content = "알림 내용",
                receiver = User(
                    email = email,
                    password = "Password1234~!",
                    name = "홍길동",
                    phone = "010-1234-5678"
                ),
                sender = "admin"
            )
        )

        Mockito.`when`(notificationRepository.findByReceiverEmailOrderByCreatedAtDesc(email, PageRequest.of(0, 10))).thenReturn(notifications)
        val emitter = notificationService.notificationSubscribe(email)

        assertNotNull(emitter)
    }

    @Test
    @DisplayName("알림 발송 - 전체 알림")
    fun sendNotificationAll() {
        // given
        val notificationRequestDto: NotificationRequestDto = NotificationRequestDto(
            title = "알림 제목",
            content = "알림 내용",
            receiver = null,
            sender = "admin"
        )
        val users: List<User> = listOf(
            User(
                email = "test@test.com",
                password = "Password1234~!",
                name = "홍길동",
                phone = "010-1234-5678"
            ),
            User(
                email = "test12@test.com",
                password = "Password1234~!",
                name = "홍길동",
                phone = "010-1234-5678"
            ),
        )

        Mockito.`when`(userRepository.findAll()).thenReturn(users)
        Mockito.`when`(notificationRepository.save(Mockito.any(Notification::class.java))).thenReturn(
            Notification(
                title = "알림 제목",
                content = "알림 내용",
                receiver = users[0],
                sender = "admin"
            )
        )

        // when
        notificationService.sendNotification(notificationRequestDto, "admin")

        // then
        Mockito.verify(notificationRepository, Mockito.times(2)).save(Mockito.any(Notification::class.java))
    }


    @Test
    @DisplayName("알림 발송 - 특정 사용자")
    fun sendNotification() {
        // given
        val notificationRequestDto: NotificationRequestDto = NotificationRequestDto(
            title = "알림 제목",
            content = "알림 내용",
            receiver = "test@test.com",
            sender = "admin"
        )
        val user: User = User(
            email = "test@test.com",
            password = "Password1234~!",
            name = "홍길동",
            phone = "010-1234-5678",
        )

        Mockito.`when`(userRepository.findById("test@test.com")).thenReturn(java.util.Optional.of(user))

        Mockito.`when`(notificationRepository.save(Mockito.any(Notification::class.java))).thenReturn(
            Notification(
                title = "알림 제목",
                content = "알림 내용",
                receiver = user,
                sender = "admin"
            )
        )

        // when
        notificationService.sendNotification(notificationRequestDto, "admin")

        // then
        Mockito.verify(notificationRepository, Mockito.times(1)).save(Mockito.any(Notification::class.java))

    }

    @Test
    @DisplayName("유저별 알림 조회")
    fun getNotifications() {
        // given
        val email: String = "test@test.com"
        val notifications: List<Notification> = listOf(
            Notification(
                title = "알림 제목",
                content = "알림 내용",
                receiver = User(
                    email = email,
                    password = "Password1234~!",
                    name = "홍길동",
                    phone = "010-1234-5678"
                ),
                sender = "admin"
            )
        )
        Mockito.`when`(notificationRepository.findByReceiverEmailOrderByCreatedAtDesc(email)).thenReturn(notifications)

        // when
        val notificationList: List<Notification> = notificationService.getNotifications(email)

        // then
        assert(notificationList.isNotEmpty())
    }

}

