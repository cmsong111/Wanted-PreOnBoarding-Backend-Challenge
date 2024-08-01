package org.project.portfolio.comment.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.comment.entity.Comment


/**
 * 댓글 응답 DTO
 */
@Schema(description = "댓글 응답 DTO")
data class CommentResponse(
    val id: Long,
    val content: String,
    val articleId: Long,
    val userId: String,
    val userName: String,
    val createdAt: String,
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
