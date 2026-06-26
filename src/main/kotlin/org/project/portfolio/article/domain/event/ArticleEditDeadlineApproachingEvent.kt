package org.project.portfolio.article.domain.event

data class ArticleEditDeadlineApproachingEvent(
    val articleId: Long,
    val articleTitle: String,
    val authorId: Long,
)
