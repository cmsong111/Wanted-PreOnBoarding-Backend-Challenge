package org.project.portfolio.article.repository

import org.project.portfolio.article.entity.Article
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {

    /**
     * 게시글 생성일자 범위로 조회하는 메소드
     */
    fun findByCreatedAtBetween(createdAtStart: Timestamp, createdAtEnd: Timestamp): List<Article>

    /**
     * 게시글 제목으로 조회하는 메소드
     * @param title 게시글 제목 (null일 경우 전체 조회)
     * @param pageable 페이징 정보 객체
     */
    @Query("SELECT a FROM Article a WHERE (:title IS NULL OR a.title LIKE %:title%)")
    fun findByTitleContains(title: String?, pageable: Pageable): List<Article>


    /**
     * 게시글 Hard 삭제
     * @param id 게시글 ID
     */
    @Modifying
    @Query("DELETE FROM article WHERE id = :id", nativeQuery = true)
    fun hardDeleteById(id: Long)
}
