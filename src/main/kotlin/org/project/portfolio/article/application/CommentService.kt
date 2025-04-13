package org.project.portfolio.article.application

import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.Comment
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.article.domain.exception.ArticleNotFoundException
import org.project.portfolio.article.domain.exception.CommentNotFoundException
import org.project.portfolio.article.presentation.response.CommentResponse
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.user.presentation.response.UserSummaryResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
) {
    /**
     * 댓글 생성 메소드
     * @param userId 유저 ID
     * @param articleId 게시글 ID
     * @param content 댓글 내용
     * @return 댓글 응답 DTO
     */
    @Transactional
    fun createComment(
        userId: Long,
        articleId: Long,
        content: String,
    ): CommentResponse {
        // 게시글이 존재하지 않는 경우 예외 처리
        if (!articleRepository.existsById(articleId)) {
            throw ArticleNotFoundException()
        }

        // 댓글 저장 및 반환
        val comment = commentRepository.save(
            Comment.create(
                content = content,
                authorId = userId,
                articleId = articleId,
            ),
        )

        return CommentResponse.from(
            comment = comment,
            author = UserSummaryResponse.from(userRepository.findByIdOrNull(userId)),
        )
    }

    /**
     * 댓글 수정 메소드
     * @param userId 유저 ID
     * @param articleId 게시글 ID
     * @param commentId 댓글 ID
     * @param content 댓글 내용
     * @return 댓글 응답 DTO
     */
    @Transactional
    fun updateComment(
        userId: Long,
        articleId: Long,
        commentId: Long,
        content: String,
    ): CommentResponse {
        // 게시글이 존재하지 않는 경우 예외 처리
        if (!articleRepository.existsById(articleId)) {
            throw ArticleNotFoundException()
        }

        val comment = commentRepository.findByArticleIdAndId(articleId, commentId)
            ?: throw CommentNotFoundException()

        comment.update(content)

        return CommentResponse.from(
            comment = comment,
            author = UserSummaryResponse.from(userRepository.findByIdOrNull(comment.authorId)),
        )
    }

    /**
     * 댓글 삭제 메소드
     * @param commentId 댓글 ID
     * @return
     */
    @Transactional
    fun deleteComment(commentId: Long) {
        commentRepository.deleteById(commentId)
    }
}
