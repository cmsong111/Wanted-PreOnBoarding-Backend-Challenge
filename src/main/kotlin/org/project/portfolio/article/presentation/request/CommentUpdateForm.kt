package org.project.portfolio.article.presentation.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "댓글 수정")
data class CommentUpdateForm(
    @field:NotBlank(message = "댓글 내용을 입력해주세요")
    @field:Size(min = 1, max = 1000, message = "댓글은 1000자를 초과할 수 없습니다")
    @field:Schema(description = "댓글 내용", example = "댓글입니다")
    val content: String? = null,
)
