package org.project.portfolio.article.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull


@Schema(description = "댓글 요청 DTO")
data class CommentForm(
    @field:Schema(description = "댓글 내용", example = "댓글입니다")
    @field:NotNull(message = "댓글 내용은 필수입니다")
    val content: String?
)
