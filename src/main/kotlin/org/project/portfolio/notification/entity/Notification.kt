package org.project.portfolio.notification.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.project.portfolio.common.entity.BaseEntity
import org.project.portfolio.user.entity.User

/**
 * 알림 엔티티
 */
@Entity
class Notification(
    /** 알림 ID */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    /** 알림 제목 */
    val title: String,
    /** 알림 내용 */
    val content: String,
    /** 알림 수신자 */
    @ManyToOne
    val receiver: User,
    /** 전송 주체 */
    val sender: String,
) : BaseEntity()
