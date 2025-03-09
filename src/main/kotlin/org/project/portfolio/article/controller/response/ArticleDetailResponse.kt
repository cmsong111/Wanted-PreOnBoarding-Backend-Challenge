package org.project.portfolio.article.controller.response

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.article.entity.Article
import org.project.portfolio.user.controller.response.UserHeaderResponse
import org.project.portfolio.user.entity.User

/** 게시글 상세 응답 DTO */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "게시글 상세 응답 DTO")
data class ArticleDetailResponse(
    @field:Schema(description = "게시글 번호")
    var id: Long,
    @field:Schema(description = "게시글 제목")
    var title: String,
    @field:Schema(description = "게시글 내용")
    var content: String,
    @field:Schema(description = "게시글 이미지들")
    val images: List<String>,
    @field:Schema(description = "댓글 들")
    val comments: List<CommentResponse>,
    @field:Schema(description = "게시글 작성자")
    var author: UserHeaderResponse,
    @field:Schema(description = "게시글 생성일시")
    var createdAt: String,
    @field:Schema(description = "게시글 수정일시")
    var updatedAt: String,
    @field:Schema(description = "조회수")
    var viewCount: Long,
) {
    companion object {
        fun from(article: Article): ArticleDetailResponse {
            return ArticleDetailResponse(
                id = article.id,
                title = article.title,
                content = article.content,
                images = article.images,
                comments = article.comments.map { CommentResponse.from(it) },
                author = UserHeaderResponse.from(article.author ?: User.createWithdrawnUser()),
                createdAt = article.createdAt.toString(),
                updatedAt = article.updatedAt.toString(),
                viewCount = article.viewCount
            )
        }
    }
}
