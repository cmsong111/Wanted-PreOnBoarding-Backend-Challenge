package org.project.portfolio.writing.tone.presentation.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * 글 스타일 변환 요청/응답 폼
 * @param title 제목
 * @param content 내용
 */
@Schema(description = "글 스타일 변환 폼")
data class ArticleWritingForm(
    @field:NotBlank(message = "제목을 입력해주세요")
    @field:Size(max = 255, message = "제목은 255자 이하로 입력해주세요")
    @field:JsonProperty(required = true, value = "title")
    @field:JsonPropertyDescription("변환된 게시글 제목 (255자 이내)")
    val title: String,
    @field:NotBlank(message = "내용을 입력해주세요")
    @field:Size(max = 2000, message = "내용은 2000자 이하로 입력해주세요")
    @field:JsonProperty(required = true, value = "description")
    @field:JsonPropertyDescription("변환된 게시글 본문 내용 (2000자 이내)")
    val content: String,
)
