package org.project.portfolio.article.presentation.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.article.domain.Article
import org.project.portfolio.user.presentation.response.UserSummaryResponse

/**
 * 게시글 헤더 응답 DTO
 */
@Schema(description = "게시글 간략 정보 응답 DTO")
data class ArticleSummaryResponse(
    /** 게시글 ID */
    @field:Schema(description = "게시글 ID", example = "1")
    val id: Long,
    /** 게시글 제목 */
    @field:Schema(description = "게시글 제목", example = "제목입니다")
    val title: String,
    @field:Schema(description = "게시글 내용 (첫 50글자)", example = "내용입니다...")
    val content: String,
    @field:Schema(description = "게시글 썸네일", example = "https://example.com/thumbnail.jpg")
    val thumbnail: String?,
    /** 게시글 작성자 이름 */
    @field:Schema(description = "게시글 작성자 이름", example = "작성자")
    val author: UserSummaryResponse,
    /** 게시글 작성일 */
    @field:Schema(description = "게시글 작성일", example = "2021-01-01T00:00:00")
    val createdAt: String,
) {
    companion object {
        fun from(
            article: Article,
            author: UserSummaryResponse,
        ): ArticleSummaryResponse {
            return ArticleSummaryResponse(
                id = article.id,
                title = article.title,
                content = article.content.take(50),
                thumbnail = article.images.firstOrNull(),
                author = author,
                createdAt = article.createdAt.toString(),
            )
        }
    }
}
