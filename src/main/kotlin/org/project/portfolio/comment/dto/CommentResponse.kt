package org.project.portfolio.comment.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.comment.entity.Comment


/**
 * 댓글 응답 DTO
 */
@Schema(description = "댓글 응답 DTO")
data class CommentResponse(
    @field:Schema(description = "댓글 번호")
    val id: Long,
    @field:Schema(description = "댓글 내용")
    val content: String,
    @field:Schema(description = "게시글 번호")
    val articleId: Long,
    @field:Schema(description = "댓글 작성자 ID")
    val userId: String,
    @field:Schema(description = "댓글 작성자 이름")
    val userName: String,
    @field:Schema(description = "댓글 생성일시")
    val createdAt: String,
    @field:Schema(description = "댓글 수정일시")
    val updatedAt: String,
) {
    constructor(comment: Comment) : this(
        id = comment.id,
        content = comment.content,
        articleId = comment.article.id,
        userId = comment.author.email,
        userName = comment.author.name,
        createdAt = comment.createdAt.toString(),
        updatedAt = comment.updatedAt.toString()
    )
}
