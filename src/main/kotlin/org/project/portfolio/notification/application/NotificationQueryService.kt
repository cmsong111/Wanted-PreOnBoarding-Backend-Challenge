package org.project.portfolio.notification.application

import org.project.portfolio.notification.domain.NotificationRepository
import org.project.portfolio.notification.presentation.response.NotificationResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NotificationQueryService(
    private val notificationRepository: NotificationRepository,
) {
    /**
     * 유저 별 알림 내역 조회
     */
    fun getNotifications(
        email: String,
        pageable: Pageable,
    ): Page<NotificationResponse> {
        return notificationRepository.findByReceiverEmailOrderByCreatedAtDesc(
            email = email,
            pageable = pageable,
        ).map(NotificationResponse::from)
    }
}
