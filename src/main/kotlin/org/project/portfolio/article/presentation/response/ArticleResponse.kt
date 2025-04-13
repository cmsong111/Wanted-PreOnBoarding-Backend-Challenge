package org.project.portfolio.article.presentation.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.article.domain.Article
import org.project.portfolio.user.presentation.response.UserSummaryResponse

/** 게시글 상세 응답 DTO */
@Schema(description = "게시글 상세 응답 DTO")
data class ArticleResponse(
    @field:Schema(description = "게시글 번호")
    val id: Long,
    @field:Schema(description = "게시글 제목")
    val title: String,
    @field:Schema(description = "게시글 내용")
    val content: String,
    @field:Schema(description = "게시글 이미지들")
    val images: List<String>,
    @field:Schema(description = "댓글 들")
    val comments: List<CommentResponse>,
    @field:Schema(description = "게시글 작성자")
    val author: UserSummaryResponse,
    @field:Schema(description = "게시글 생성일시")
    val createdAt: String,
    @field:Schema(description = "게시글 수정일시")
    val updatedAt: String,
    @field:Schema(description = "조회수")
    val viewCount: Long,
) {
    companion object {
        fun from(
            article: Article,
            author: UserSummaryResponse,
            comments: List<CommentResponse> = emptyList(),
        ): ArticleResponse {
            return ArticleResponse(
                id = article.id,
                title = article.title,
                content = article.content,
                images = article.images,
                comments = comments,
                author = author,
                createdAt = article.createdAt.toString(),
                updatedAt = article.updatedAt.toString(),
                viewCount = article.viewCount,
            )
        }
    }
}
