package org.project.portfolio.article.repository

import org.project.portfolio.article.entity.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {

    /**
     * 게시글 생성일자 범위로 조회하는 메소드
     */
    fun findByCreatedAtBetween(createdAtStart: Timestamp, createdAtEnd: Timestamp): List<Article>
}
