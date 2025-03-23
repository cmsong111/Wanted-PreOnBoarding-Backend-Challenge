package org.project.portfolio.article.controller.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "댓글 요청 DTO")
data class CommentForm(
    @field:NotBlank(message = "댓글 내용을 입력해주세요")
    @field:Schema(description = "댓글 내용", example = "댓글입니다")
    val content: String,
)
