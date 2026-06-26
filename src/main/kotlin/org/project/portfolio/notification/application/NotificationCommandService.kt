package org.project.portfolio.notification.application

import org.project.portfolio.notification.domain.Notification
import org.project.portfolio.notification.domain.NotificationRepository
import org.project.portfolio.notification.presentation.request.NotificationRequestDto
import org.project.portfolio.notification.presentation.response.NotificationResponse
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class NotificationCommandService(
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
    private val notificationSseService: NotificationSseService,
) {
    /** 알림 발송 (단건 또는 전체) */
    fun sendNotification(
        notificationRequestDto: NotificationRequestDto,
        sender: String,
    ) {
        val receiverId = notificationRequestDto.receiver
        if (receiverId == null) {
            userRepository.findAll().forEach { user ->
                val saved = saveNotification(notificationRequestDto, receiver = user, sender = sender)
                notificationSseService.sendToUser(user.email, NotificationResponse.from(saved))
            }
        } else {
            userRepository.findByIdOrNull(receiverId)?.let { user ->
                val saved = saveNotification(notificationRequestDto, receiver = user, sender = sender)
                notificationSseService.sendToUser(user.email, NotificationResponse.from(saved))
            }
        }
    }

    private fun saveNotification(
        notificationRequestDto: NotificationRequestDto,
        receiver: User,
        sender: String,
    ): Notification {
        val notification = Notification(
            title = notificationRequestDto.title,
            content = notificationRequestDto.content,
            receiver = receiver,
            sender = notificationRequestDto.sender ?: sender,
        )
        return notificationRepository.save(notification)
    }
}
