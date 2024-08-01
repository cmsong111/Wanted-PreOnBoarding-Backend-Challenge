package org.project.portfolio.article.dto

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.article.entity.Article

/**
 * 게시글 응답 DTO (헤더 정보)
 */
@Schema(description = "게시글 응답 (헤더 정보)")
data class ArticleResponseHeader(
    /** 게시글 ID */
    @field:Schema(description = "게시글 ID", example = "1")
    val id: Long,
    /** 게시글 제목 */
    @field:Schema(description = "게시글 제목", example = "제목입니다")
    val title: String,
    /** 게시글 작성자 이름 */
    @field:Schema(description = "게시글 작성자 이름", example = "작성자")
    val authorName: String,
    /** 게시글 작성일 */
    @field:Schema(description = "게시글 작성일", example = "2021-01-01T00:00:00")
    val createdAt: String
) {
    constructor(article: Article) : this(
        id = article.id,
        title = article.title,
        authorName = article.author.name,
        createdAt = article.createdAt.toString()
    )
}
