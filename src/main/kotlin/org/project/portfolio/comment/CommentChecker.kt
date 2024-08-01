package org.project.portfolio.comment

import org.project.portfolio.comment.entity.Comment
import org.project.portfolio.comment.repository.CommentRepository
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.exception_handler.ErrorCode
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class CommentChecker(
    private val commentRepository: CommentRepository
) {

    /**
     * Check if the user is the author of the comment
     * @param commentId Comment ID
     * @return Whether the user is the author of the comment (true: author, false: not author)
     */
    fun isAuthor(commentId: Long): Boolean {
        val comment: Comment = commentRepository.findById(commentId).orElseThrow() {
            BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        }

        return comment.author.email == SecurityContextHolder.getContext().authentication.name
    }

    /**
     * Check if the comment is related to the article
     */
    fun isArticleComment(articleId: Long, commentId: Long): Boolean {
        val comment: Comment = commentRepository.findById(commentId).orElseThrow() {
            BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        }

        return comment.article.id == articleId
    }

    /**
     * Check if the comment is editable
     */
    fun isEditable(articleId: Long, commentId: Long): Boolean {
        return isAuthor(commentId) && isArticleComment(articleId, commentId)
    }

}
