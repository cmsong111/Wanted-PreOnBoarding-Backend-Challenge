package org.project.portfolio.notification.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull

/**
 * 알림 요청 DTO
 */
@Schema(name = "알림 요청 폼")
data class NotificationRequestDto(
    /** 제목 */
    @field:Schema(description = "제목", example = "알림 제목")
    @field:NotNull(message = "제목을 입력해주세요")
    val title: String?,

    /** 내용 */
    @field:Schema(description = "내용", example = "알림 내용")
    @field:NotNull(message = "내용을 입력해주세요")
    val content: String?,

    /** 수신자 */
    @field:Schema(description = "수신자(미 입력시 전체 알림)", example = "user@test.com")
    val receiver: String?,

    /** 전송 주체 */
    @field:Schema(description = "전송 주체", example = "게시글 관리자")
    val sender: String?
) {
}
