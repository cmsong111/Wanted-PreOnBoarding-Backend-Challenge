package org.project.portfolio.article.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.project.portfolio.article.application.ArticleAuthorService
import org.project.portfolio.article.presentation.request.ArticleForm
import org.project.portfolio.article.presentation.request.ArticleUpdateForm
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.config.SwaggerConfig.Companion.ARTICLE_API_TAG
import org.project.portfolio.config.SwaggerConfig.Companion.BEARER_AUTH
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

/**
 * 게시글 작성자 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/articles")
@Tag(name = ARTICLE_API_TAG, description = "The article API")
class ArticleAuthorController(
    private val articleService: ArticleAuthorService,
) {
    @PostMapping(consumes = ["multipart/form-data"])
    @Operation(summary = "게시글 생성 API")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("isAuthenticated()")
    fun createArticle(
        @AuthenticationPrincipal userDetails: UserDetails,
        @Valid @ModelAttribute articleForm: ArticleForm,
    ): ResponseEntity<ArticleResponse> {
        return articleService.createArticle(
            email = userDetails.username,
            articleForm = articleForm,
        ).let {
            ResponseEntity
                .created(URI.create("/api/v1/article/${it.id}"))
                .body(it)
        }
    }

    @PatchMapping("/{articleId}", consumes = ["multipart/form-data"])
    @Operation(summary = "게시글 수정 API")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @articleValidator.isEditable(#articleId, #userDetails.username))")
    fun updateArticle(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable @Parameter(description = "게시글 ID") articleId: Long,
        @Valid @ModelAttribute articleUpdateForm: ArticleUpdateForm,
    ): ResponseEntity<ArticleResponse> {
        return ResponseEntity.ok(
            articleService.updateArticle(
                articleId = articleId,
                articleUpdateForm = articleUpdateForm,
            ),
        )
    }

    @DeleteMapping("/{articleId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @articleValidator.isEditable(#articleId, #userDetails.username))")
    @Operation(summary = "게시글 삭제 API")
    @SecurityRequirement(name = BEARER_AUTH)
    fun deleteArticle(
        @AuthenticationPrincipal userDetails: UserDetails,
        @PathVariable @Parameter(description = "게시글 ID") articleId: Long,
    ): ResponseEntity<Unit> {
        articleService.deleteArticle(articleId)
        return ResponseEntity.noContent().build()
    }
}
