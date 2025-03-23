package org.project.portfolio.article.entity.validator

import org.project.portfolio.article.entity.Comment
import org.project.portfolio.article.repository.ArticleJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class CommentValidator(
    private val articleRepository: ArticleJpaRepository,
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
        val comment: Comment = articleRepository.findByIdOrNull(articleId)?.comments?.find {
            it.id == commentId
        } ?: return false

        return comment.author.id == userId
    }
}
