package org.project.portfolio.comment.repository

import org.project.portfolio.comment.entity.Comment
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {

    /** 게시글 ID로 댓글 조회 메소드 */
    fun findByArticleId(id: Long, sort: Sort): List<Comment>
}
