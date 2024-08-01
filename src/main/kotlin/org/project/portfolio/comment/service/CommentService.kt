package org.project.portfolio.comment.service

import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleRepository
import org.project.portfolio.comment.dto.CommentRequest
import org.project.portfolio.comment.dto.CommentResponse
import org.project.portfolio.comment.entity.Comment
import org.project.portfolio.comment.repository.CommentRepository
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.exception_handler.ErrorCode
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository
) {
    /**
     * 댓글 생성 메소드
     * @param name 유저 이름
     * @param articleId 게시글 ID
     * @param commentRequest 댓글 요청 DTO
     * @return 댓글 응답 DTO
     */
    fun createComment(name: String, articleId: Long, commentRequest: CommentRequest): CommentResponse {
        // 게시글이 존재하지 않는 경우 예외 처리
        val article: Article = articleRepository.findById(articleId).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }
        // 유저가 존재하지 않는 경우 예외 처리
        val user: User = userRepository.findById(name).orElseThrow() {
            BusinessException(ErrorCode.USER_NOT_FOUND)
        }
        // 댓글 생성
        val comment: Comment = Comment(
            article = article,
            author = user,
            content = commentRequest.content!!
        )
        // 댓글 저장 및 반환
        return CommentResponse(comment = commentRepository.save(comment))
    }

    /**
     * 댓글 수정 메소드
     * @param articleId 게시글 ID
     * @param id 댓글 ID
     * @param commentRequest 댓글 요청 DTO
     * @return 댓글 응답 DTO
     */
    fun updateComment(articleId: Long, id: Long, commentRequest: CommentRequest): CommentResponse {
        // 게시글이 존재하지 않는 경우 예외 처리
        articleRepository.findById(articleId).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }

        // 댓글이 존재하지 않는 경우 예외 처리
        val comment: Comment = commentRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        }

        // 댓글과 게시글의 관계가 일치하지 않는 경우 예외 처리
        if (comment.article.id != articleId) {
            throw BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        }

        // 댓글 수정
        comment.update(content = commentRequest.content!!)

        // 댓글 저장 및 반환
        return CommentResponse(comment = commentRepository.save(comment))
    }

    /**
     * 댓글 삭제 메소드
     * @param articleId 게시글 ID
     * @param id 댓글 ID
     * @return 댓글 응답 DTO
     * @throws BusinessException 게시글이 존재하지 않거나 댓글이 존재하지 않는 경우 예외 처리
     * @throws BusinessException 댓글과 게시글의 관계가 일치하지 않는 경우 예외 처리
     * @throws BusinessException 댓글 삭제 권한이 없는 경우 예외 처리
     */
    fun deleteComment(articleId: Long, id: Long) {
        // 게시글이 존재하지 않는 경우 예외 처리
        articleRepository.findById(articleId).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }

        // 댓글이 존재하지 않는 경우 예외 처리
        val comment: Comment = commentRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        }

        // 댓글과 게시글의 관계가 일치하지 않는 경우 예외 처리
        if (comment.article.id != articleId) {
            throw BusinessException(ErrorCode.COMMENT_NOT_FOUND)
        }

        // 댓글 삭제
        commentRepository.deleteById(id)
    }
}
