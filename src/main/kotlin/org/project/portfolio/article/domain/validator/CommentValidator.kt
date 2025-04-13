package org.project.portfolio.article.domain.validator

import org.project.portfolio.article.domain.Comment
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.article.domain.exception.CommentNotFoundException
import org.springframework.stereotype.Component

@Component
class CommentValidator(
    private val commentRepository: CommentRepository,
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
        userId: Long,
    ): Boolean {
        val comment: Comment = commentRepository.findByArticleIdAndId(articleId, commentId)
            ?: throw CommentNotFoundException()

        return comment.authorId == userId
    }
}
