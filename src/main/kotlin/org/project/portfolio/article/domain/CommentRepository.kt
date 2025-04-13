package org.project.portfolio.article.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {
    /**
     * 게시글 ID로 댓글 조회
     * @param articleId 게시글 ID
     * @return 댓글 리스트
     */
    fun findByArticleId(articleId: Long): List<Comment>

    /**
     * 게시글 ID + 댓글 ID로 특정 댓글 조회
     * @param id 댓글 ID
     * @param articleId 게시글 ID
     * @return 댓글
     */
    fun findByArticleIdAndId(
        articleId: Long,
        id: Long,
    ): Comment?
}
