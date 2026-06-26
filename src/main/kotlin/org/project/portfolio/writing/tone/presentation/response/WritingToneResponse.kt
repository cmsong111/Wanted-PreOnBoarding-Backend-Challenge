package org.project.portfolio.writing.tone.presentation.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.writing.tone.domain.WritingTone
import java.time.Instant

@Schema(description = "말투 분석 응답 DTO")
data class WritingToneResponse(
    @field:Schema(description = "말투 분석 ID")
    val id: Long,
    @field:Schema(description = "사용자 ID")
    val userId: Long,
    @field:Schema(description = "공개 여부")
    val isPublic: Boolean,
    @field:Schema(description = "말투 분석 설명")
    val description: String,
    @field:Schema(description = "생성일시")
    val createdAt: Instant,
) {
    companion object {
        fun from(writingTone: WritingTone): WritingToneResponse {
            return WritingToneResponse(
                id = writingTone.id,
                userId = writingTone.userId,
                isPublic = writingTone.isPublic,
                description = writingTone.description,
                createdAt = writingTone.createdAt,
            )
        }
    }
}
