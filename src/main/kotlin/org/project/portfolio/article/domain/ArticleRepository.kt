package org.project.portfolio.article.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant

@Repository
interface ArticleRepository : JpaRepository<Article, Long> {
    /**
     * 게시글 생성일자 범위로 조회하는 메소드
     */
    fun findByCreatedAtBetween(
        createdAtStart: Instant,
        createdAtEnd: Instant,
    ): List<Article>

    /**
     * 게시글 제목으로 조회하는 메소드
     * @param title 검색할 게시글 제목(null일 경우 전체 조회)
     * @param content 검색할 게시글 내용(null일 경우 전체 조회)
     * @param pageable 페이징 정보 객체
     */
    @Query(
        """
    SELECT a FROM Article a
    WHERE (:title IS NULL OR a.title LIKE CONCAT('%', :title, '%'))
    OR (:content IS NULL OR a.content LIKE CONCAT('%', :content, '%'))
    """,
    )
    fun findByArticleTitleAndContent(
        @Param("title") title: String? = null,
        @Param("content") content: String? = null,
        pageable: Pageable,
    ): Page<Article>

    /**
     * 조회수 증가 메소드
     * @param id 게시글 ID
     */
    @Modifying
    @Query(
        """
        update Article a
        set a.viewCount = a.viewCount + 1
        where a.id = :id""",
    )
    fun increaseViewCount(id: Long): Int

    /**
     * 작성자 ID로 게시글 조회 메소드
     * @param authorId 작성자 ID
     * @param pageable 페이징 정보 객체
     */
    fun findByAuthorId(
        authorId: Long,
        pageable: Pageable,
    ): Page<Article>
}
