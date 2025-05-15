package org.project.portfolio.article.domain.validator

import org.project.portfolio.article.domain.Comment
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.springframework.stereotype.Component

@Component
class CommentValidator(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
) {
    /**
     * 댓글 작성자가 맞는지 확인
     * @param articleId 게시글 ID
     * @param commentId 댓글 ID
     * @param userId 유저 ID
     */
    fun isAuthor(
        articleId: Long,
        commentId: Long,
        email: String,
    ): Boolean {
        val comment: Comment = commentRepository.findByArticleIdAndId(articleId, commentId)
            ?: throw NotFoundException(Comment::class.java, mapOf("articleId" to articleId, "commentId" to commentId))

        val user = userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))

        return comment.authorId == user.id
    }
}
