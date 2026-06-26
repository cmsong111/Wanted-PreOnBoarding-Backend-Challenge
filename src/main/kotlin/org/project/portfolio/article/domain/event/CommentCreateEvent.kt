package org.project.portfolio.article.domain.event

data class CommentCreateEvent(
    val articleId: Long,
    val articleTitle: String,
    val articleAuthorId: Long,
    val commentId: Long,
    val commentAuthorId: Long,
    val commentAuthorName: String,
    val content: String,
)
