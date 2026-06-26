package org.project.portfolio.article.application

import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.Comment
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.article.domain.event.CommentCreateEvent
import org.project.portfolio.article.presentation.response.CommentResponse
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.user.presentation.response.UserSummaryResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentCommandService(
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {
    /**
     * 댓글 생성
     */
    fun createComment(
        email: String,
        articleId: Long,
        content: String,
    ): CommentResponse {
        val article = articleRepository.findByIdOrNull(articleId)
            ?: throw NotFoundException(Article::class.java, mapOf("articleId" to articleId))

        val user: User = userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))

        val comment = commentRepository.save(
            Comment.create(
                content = content,
                authorId = user.id,
                articleId = articleId,
            ),
        )

        if (article.authorId != user.id) {
            eventPublisher.publishEvent(
                CommentCreateEvent(
                    articleId = article.id,
                    articleTitle = article.title,
                    articleAuthorId = article.authorId,
                    commentId = comment.id,
                    commentAuthorId = user.id,
                    commentAuthorName = user.name,
                    content = comment.content,
                ),
            )
        }

        return CommentResponse.from(
            comment = comment,
            author = UserSummaryResponse.from(user),
        )
    }

    /**
     * 댓글 수정
     */
    fun updateComment(
        email: String,
        articleId: Long,
        commentId: Long,
        content: String,
    ): CommentResponse {
        if (!articleRepository.existsById(articleId)) {
            throw NotFoundException(Article::class.java, mapOf("articleId" to articleId))
        }

        val comment = commentRepository.findByArticleIdAndId(articleId, commentId)
            ?: throw NotFoundException(Comment::class.java, mapOf("articleId" to articleId, "commentId" to commentId))

        comment.update(content)

        return CommentResponse.from(
            comment = comment,
            author = UserSummaryResponse.from(userRepository.findByIdOrNull(comment.authorId)),
        )
    }

    /**
     * 댓글 삭제
     */
    fun deleteComment(commentId: Long) {
        commentRepository.deleteById(commentId)
    }
}
