package org.project.portfolio.notification.presentation.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.notification.domain.Notification
import java.time.Instant

/** 알림 응답 DTO */
@Schema(description = "알림 응답 DTO")
data class NotificationResponse(
    @field:Schema(description = "알림 ID")
    val id: Long,
    @field:Schema(description = "알림 제목")
    val title: String,
    @field:Schema(description = "알림 내용")
    val content: String,
    @field:Schema(description = "수신자 ID")
    val receiverId: Long,
    @field:Schema(description = "전송 주체")
    val sender: String,
    @field:Schema(description = "생성일시")
    val createdAt: Instant,
) {
    companion object {
        fun from(notification: Notification): NotificationResponse {
            return NotificationResponse(
                id = notification.id,
                title = notification.title,
                content = notification.content,
                receiverId = notification.receiver.id,
                sender = notification.sender,
                createdAt = notification.createdAt,
            )
        }
    }
}
