package org.project.portfolio.article.service

import org.project.portfolio.article.controller.response.CommentResponse
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleJpaRepository
import org.project.portfolio.common.exception.BusinessException
import org.project.portfolio.common.exception.ErrorCode
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CommentService(
    private val articleRepository: ArticleJpaRepository,
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
        val article: Article = articleRepository.findByIdOrNull(articleId)
            ?: throw BusinessException(ErrorCode.ARTICLE_NOT_FOUND)

        // 댓글 저장 및 반환
        return CommentResponse.from(
            comment = article.addComment(
                content = content,
                author = userRepository.findByIdOrNull(userId)
                    ?: throw BusinessException(ErrorCode.USER_NOT_FOUND),
            ),
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
    fun updateComment(
        userId: Long,
        articleId: Long,
        commentId: Long,
        content: String,
    ): CommentResponse {
        // 게시글이 존재하지 않는 경우 예외 처리
        val article: Article = articleRepository.findByIdOrNull(articleId) ?: throw BusinessException(ErrorCode.ARTICLE_NOT_FOUND)

        // 유저가 존재하지 않는 경우 예외 처리
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        // 댓글 수정 및 반환
        return CommentResponse.from(
            article.updateComment(commentId, content) ?: throw BusinessException(ErrorCode.COMMENT_NOT_FOUND),
        )
    }

    /**
     * 댓글 삭제 메소드
     * @param email 유저 이메일
     * @param articleId 게시글 ID
     * @param commentId 댓글 ID
     * @return
     */
    @Transactional
    fun deleteComment(
        userId: Long,
        articleId: Long,
        commentId: Long,
    ) {
        // 댓글 삭제
        val article: Article = articleRepository.findByIdOrNull(articleId) ?: throw BusinessException(ErrorCode.ARTICLE_NOT_FOUND)

        // 유저가 존재하지 않는 경우 예외 처리
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        // 댓글 삭제
        article.removeComment(commentId)
    }
}
