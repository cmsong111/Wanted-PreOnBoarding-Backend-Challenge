package org.project.portfolio.article.presentation.request

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.web.multipart.MultipartFile

/** 게시글 요청 DTO */
@Schema(description = "게시글 요청")
data class ArticleUpdateForm(
    /** 게시글 제목 */
    @field:Schema(description = "게시글 제목", example = "제목입니다")
    val title: String? = null,
    /** 게시글 내용 */
    @field:Schema(description = "게시글 내용", example = "내용입니다")
    val content: String? = null,
    /** 게시글 사진 */
    @field:Schema(description = "게시글 사진 (수정이 필요하면 이미지를 첨부해주세요. 삭제는 API를 이용해주세요.)")
    val images: List<MultipartFile>? = null,
)
