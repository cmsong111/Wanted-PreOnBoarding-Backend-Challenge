package org.project.portfolio.notification.presentation.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

/** 알림 요청 DTO */
@Schema(description = "알림 요청 폼")
data class NotificationRequestDto(
    /** 제목 */
    @field:Schema(description = "제목", example = "알림 제목")
    @field:NotBlank(message = "제목을 입력해주세요")
    val title: String,
    /** 내용 */
    @field:Schema(description = "내용", example = "알림 내용")
    @field:NotBlank(message = "내용을 입력해주세요")
    val content: String,
    /** 수신자 */
    @field:Schema(description = "수신자 ID (null이면 전체 발송)", example = "1")
    val receiver: Long? = null,
    /** 전송 주체 */
    @field:Schema(description = "전송 주체", example = "게시글 관리자")
    val sender: String? = null,
)
