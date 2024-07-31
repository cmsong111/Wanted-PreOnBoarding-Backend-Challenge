package org.project.portfolio.notification.repository

import org.project.portfolio.notification.entity.Notification
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/** 알림 레포지토리 */
@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {

    /** 수신자 이메일로 알림 조회 (페이징) */
    fun findByReceiverEmailOrderByCreatedAtDesc(email: String, pageable: Pageable): List<Notification>
    fun findByReceiverEmailOrderByCreatedAtDesc(email: String): List<Notification>

}
