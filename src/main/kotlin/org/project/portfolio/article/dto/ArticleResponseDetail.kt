package org.project.portfolio.article.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.entity.Image
import org.project.portfolio.comment.dto.CommentResponse
import org.project.portfolio.comment.entity.Comment

/** 게시글 상세 응답 DTO */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "게시글 상세 응답 DTO")
data class ArticleResponseDetail(
    @field:Schema(description = "게시글 번호")
    var id: Long,
    @field:Schema(description = "게시글 제목")
    var title: String,
    @field:Schema(description = "게시글 내용")
    var content: String,
    @field:Schema(description = "게시글 작성자")
    var author: String,
    @field:Schema(description = "게시글 생성일시")
    var createdAt: String,
    @field:Schema(description = "게시글 수정일시")
    var updatedAt: String,
    @field:Schema(description = "게시글 수정 가능 일시")
    var updatability: String,
    @field:Schema(description = "조회수")
    var viewCount: Long,
    @field:Schema(description = "댓글 목록")
    var commentList: List<CommentResponse>,
    @field:Schema(description = "이미지 URL")
    var image: String? = null,
) {
    constructor(article: Article, commentList: List<Comment>, image: Image? = null) : this(
        id = article.id,
        title = article.title,
        content = article.content,
        author = article.author.name,
        createdAt = article.createdAt.toString(),
        updatedAt = article.updatedAt.toString(),
        updatability = article.createdAt.toLocalDateTime().plusDays(7).toString(),
        viewCount = article.viewCount,
        commentList = commentList.map { CommentResponse(it) },
        image = image?.url
    )
}
