package org.project.portfolio.article.application

import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.article.presentation.response.CommentResponse
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.user.presentation.response.UserSummaryResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentQueryService(
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
) {
    /** 게시글별 댓글 페이징 조회 (작성자 배치 조회로 N+1 방지) */
    fun getComments(
        articleId: Long,
        pageable: Pageable,
    ): Page<CommentResponse> {
        if (!articleRepository.existsById(articleId)) {
            throw NotFoundException(Article::class.java, mapOf("articleId" to articleId))
        }

        val comments = commentRepository.findByArticleId(articleId, pageable)
        val authorIds = comments.content.map { it.authorId }.distinct()
        val authorsById = userRepository.findAllById(authorIds).associateBy { it.id }

        return comments.map { comment ->
            CommentResponse.from(
                comment = comment,
                author = UserSummaryResponse.from(authorsById[comment.authorId]),
            )
        }
    }
}
