package org.project.portfolio.article.domain.event

class CommentCreateEvent(
    val articleId: Long,
    val commentId: Long,
    val userId: Long,
    val content: String,
)
