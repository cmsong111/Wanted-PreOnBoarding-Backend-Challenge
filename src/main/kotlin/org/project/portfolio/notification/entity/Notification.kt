package org.project.portfolio.notification.entity

import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.SoftDelete
import org.project.portfolio.user.domain.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.AbstractAggregateRoot
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

/**
 * 알림 엔티티
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@SoftDelete(columnName = "is_deleted")
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
    @CreatedDate
    var createdAt: Instant = Instant.now(),
    @LastModifiedDate
    var updatedAt: Instant = Instant.now(),
) : AbstractAggregateRoot<Notification>()
