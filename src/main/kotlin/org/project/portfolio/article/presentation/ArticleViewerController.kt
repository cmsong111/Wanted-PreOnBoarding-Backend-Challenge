package org.project.portfolio.article.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.project.portfolio.article.application.ArticleReaderService
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.article.presentation.response.ArticleSummaryResponse
import org.project.portfolio.config.SwaggerConfig.Companion.ARTICLE_API_TAG
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 게시글 조회 API
 */
@RestController
@RequestMapping("/api/v1/articles")
@Tag(name = ARTICLE_API_TAG, description = "The article API")
class ArticleViewerController(
    private val articleReaderService: ArticleReaderService,
) {
    @GetMapping
    @Operation(summary = "게시글 조회 API")
    fun getArticles(
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC)
        @ParameterObject pageable: Pageable,
        @RequestParam(required = false) keyword: String?,
    ): ResponseEntity<PagedModel<ArticleSummaryResponse>> {
        return ResponseEntity.ok(
            PagedModel(
                articleReaderService.getArticles(
                    pageable = pageable,
                    keyword = keyword,
                ),
            ),
        )
    }

    @GetMapping("/{articleId}")
    @Operation(summary = "게시글 상세 조회 API")
    fun getArticle(
        @PathVariable @Parameter(description = "게시글 ID") articleId: Long,
        request: HttpServletRequest,
    ): ResponseEntity<ArticleResponse> {
        articleReaderService.increaseArticleViewCount(
            articleId = articleId,
            ip = request.remoteAddr,
            userAgent = request.getHeader("User-Agent") ?: "Unknown User-Agent",
        )
        return ResponseEntity.ok(
            articleReaderService.getArticle(articleId),
        )
    }
}
