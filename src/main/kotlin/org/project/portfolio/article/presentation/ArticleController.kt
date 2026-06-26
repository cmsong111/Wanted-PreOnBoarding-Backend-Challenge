package org.project.portfolio.article.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.project.portfolio.article.application.ArticleCommandService
import org.project.portfolio.article.application.ArticleQueryService
import org.project.portfolio.article.presentation.request.ArticleForm
import org.project.portfolio.article.presentation.request.ArticleUpdateForm
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.article.presentation.response.ArticleSummaryResponse
import org.project.portfolio.config.SwaggerConfig.Companion.ARTICLE_API_TAG
import org.project.portfolio.config.SwaggerConfig.Companion.BEARER_AUTH
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/** 게시글 API 컨트롤러 */
@RestController
@RequestMapping("/api/v1/articles")
@Tag(name = ARTICLE_API_TAG, description = "The article API")
class ArticleController(
    private val articleQueryService: ArticleQueryService,
    private val articleCommandService: ArticleCommandService,
) {
    @GetMapping
    @Operation(summary = "게시글 목록 조회 API")
    fun getArticles(
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC)
        @ParameterObject pageable: Pageable,
        @RequestParam(required = false) keyword: String?,
    ): ResponseEntity<PagedModel<ArticleSummaryResponse>> {
        return ResponseEntity.ok(
            PagedModel(
                articleQueryService.getArticles(
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
        articleQueryService.increaseArticleViewCount(
            articleId = articleId,
            ip = request.remoteAddr,
            userAgent = request.getHeader("User-Agent") ?: "Unknown User-Agent",
        )
        return ResponseEntity.ok(
            articleQueryService.getArticle(articleId),
        )
    }

    @PostMapping(consumes = ["multipart/form-data"])
    @Operation(summary = "게시글 생성 API")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("isAuthenticated()")
    fun createArticle(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @ModelAttribute articleForm: ArticleForm,
    ): ResponseEntity<ArticleResponse> {
        return articleCommandService.createArticle(
            email = jwt.subject,
            articleForm = articleForm,
        ).let {
            ResponseEntity
                .created(URI.create("/api/v1/articles/${it.id}"))
                .body(it)
        }
    }

    @PatchMapping("/{articleId}", consumes = ["multipart/form-data"])
    @Operation(summary = "게시글 수정 API")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @articleValidator.isEditable(#articleId, authentication.name))")
    fun updateArticle(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable @Parameter(description = "게시글 ID") articleId: Long,
        @Valid @ModelAttribute articleUpdateForm: ArticleUpdateForm,
    ): ResponseEntity<ArticleResponse> {
        return ResponseEntity.ok(
            articleCommandService.updateArticle(
                articleId = articleId,
                articleUpdateForm = articleUpdateForm,
            ),
        )
    }

    @DeleteMapping("/{articleId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @articleValidator.isAuthor(#articleId, authentication.name))")
    @Operation(summary = "게시글 삭제 API")
    @SecurityRequirement(name = BEARER_AUTH)
    fun deleteArticle(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable @Parameter(description = "게시글 ID") articleId: Long,
    ): ResponseEntity<Unit> {
        articleCommandService.deleteArticle(articleId)
        return ResponseEntity.noContent().build()
    }
}
