package org.project.portfolio.article.domain.exception

import org.project.portfolio.common.domain.exception.CustomException
import org.project.portfolio.common.domain.exception.NotFoundException

data class ArticleException(
    override val message: String = "Article default exception",
) : CustomException("ARTICLE_EXCEPTION", message)

data class ArticleNotFoundException(
    override val message: String = "Article not found",
) : NotFoundException(message = message)

data class CommentNotFoundException(
    override val message: String = "Comment not found",
) : NotFoundException(message = message)
