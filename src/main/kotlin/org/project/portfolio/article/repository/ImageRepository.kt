package org.project.portfolio.article.repository

import org.project.portfolio.article.entity.Image
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImageRepository : JpaRepository<Image, Long> {

    /**
     * 게시글 ID로 이미지 조회 메소드
     * @param articleId 게시글 ID
     * @return 이미지 리스트
     */
    fun findByArticleId(articleId: Long): Image?
}
