package org.project.portfolio.article.controller.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.article.entity.Comment
import org.project.portfolio.user.controller.response.UserHeaderResponse

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
    val author: UserHeaderResponse,
    @field:Schema(description = "댓글 생성일시")
    val createdAt: String,
    @field:Schema(description = "댓글 수정일시")
    val updatedAt: String,
) {
    companion object {
        fun from(comment: Comment) =
            CommentResponse(
                id = comment.id,
                content = comment.content,
                author = UserHeaderResponse.from(comment.author),
                createdAt = comment.createdAt.toString(),
                updatedAt = comment.updatedAt.toString(),
            )
    }
}
