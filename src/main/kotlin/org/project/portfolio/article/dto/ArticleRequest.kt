package org.project.portfolio.article.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length
import org.springframework.web.multipart.MultipartFile

/** 게시글 요청 DTO */
@Schema(description = "게시글 요청")
data class ArticleRequest(
    /** 게시글 제목 */
    @field:Schema(description = "게시글 제목", example = "제목입니다")
    @field:Length(min = 1, max = 200, message = "제목은 1자 이상 200자 이하로 입력해주세요")
    val title: String?,

    /** 게시글 내용 */
    @field:Schema(description = "게시글 내용", example = "내용입니다")
    @field:Length(min = 1, max = 2000, message = "내용은 1자 이상 2000자 이하로 입력해주세요")
    val content: String?,

    /** 게시글 사진 */
    @field:Schema(description = "게시글 사진", example = "사진입니다")
    val image: MultipartFile?
)
