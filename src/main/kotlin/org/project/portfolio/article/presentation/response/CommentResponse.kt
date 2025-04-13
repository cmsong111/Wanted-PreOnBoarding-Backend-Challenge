package org.project.portfolio.article.presentation.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.article.domain.Comment
import org.project.portfolio.user.presentation.response.UserSummaryResponse

/**
 * 댓글 응답 DTO
 */
@Schema(description = "댓글 응답 DTO")
data class CommentResponse(
    @field:Schema(description = "댓글 번호")
    val id: Long,
    @field:Schema(description = "댓글 내용")
    val content: String,
    @field:Schema(description = "댓글 작성자 ID")
    val author: UserSummaryResponse,
    @field:Schema(description = "댓글 생성일시")
    val createdAt: String,
    @field:Schema(description = "댓글 수정일시")
    val updatedAt: String,
) {
    companion object {
        fun from(
            comment: Comment,
            author: UserSummaryResponse,
        ): CommentResponse {
            return CommentResponse(
                id = comment.id,
                content = comment.content,
                author = author,
                createdAt = comment.createdAt.toString(),
                updatedAt = comment.updatedAt.toString(),
            )
        }
    }
}
