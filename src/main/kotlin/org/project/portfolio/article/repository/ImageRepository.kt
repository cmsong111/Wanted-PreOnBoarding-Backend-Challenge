package org.project.portfolio.article.repository

import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.entity.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface ImageRepository : JpaRepository<Image, Long> {

    /**
     * 게시글 ID로 이미지 조회 메소드
     * @param articleId 게시글 ID
     * @return 이미지 리스트
     */
    fun findByArticleId(articleId: Long): Image?




    @Transactional
    @Modifying
    @Query("update Image i set i.url = ?1 where i.article = ?2")
    fun updateUrlByArticle(url: String, article: Article): Int


    fun existsByArticle(article: Article): Boolean
}
