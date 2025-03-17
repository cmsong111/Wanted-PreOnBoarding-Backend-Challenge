package org.project.portfolio.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.ServletRequest
import jakarta.validation.Valid
import java.net.URI
import org.project.portfolio.article.controller.request.ArticleForm
import org.project.portfolio.article.controller.response.ArticleDetailResponse
import org.project.portfolio.article.controller.response.ArticleHeaderResponse
import org.project.portfolio.article.service.ArticleService
import org.project.portfolio.auth.AuthenticatedUser
import org.project.portfolio.config.SwaggerConfig.Companion.BEARER_AUTH
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 게시글 컨트롤러
 * <p>스프링 시큐리티에서 @GET 요청에 한해 permitAll 적용</p>
 */
@RestController
@RequestMapping("/api/v1/articles")
@Tag(name = "3. Article", description = "The article API")
class ArticleController(
    private val articleService: ArticleService,
) {
    @GetMapping
    @Operation(summary = "게시글 조회 API")
    fun getArticles(
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC)
        @ParameterObject pageable: PageRequest,
        @RequestParam(required = false) title: String?,
    ): ResponseEntity<Page<ArticleHeaderResponse>> {
        return ResponseEntity.ok(
            articleService.getArticles(
                pageable = pageable,
                title = title,
            ),
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회 API")
    fun getArticle(
        @PathVariable @Parameter(description = "게시글 ID") id: Long,
        servletRequest: ServletRequest,
    ): ResponseEntity<ArticleDetailResponse> {
        return ResponseEntity.ok(articleService.getArticle(id, servletRequest.remoteAddr))
    }

    @PostMapping(consumes = ["multipart/form-data"])
    @Operation(summary = "게시글 생성 API")
    @SecurityRequirement(name = BEARER_AUTH)
    fun createArticle(
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
        @Valid @ModelAttribute articleForm: ArticleForm,
    ): ResponseEntity<ArticleDetailResponse> {
        return articleService.createArticle(
            email = authenticatedUser.email,
            articleForm = articleForm,
        ).let {
            ResponseEntity
                .created(URI.create("/api/v1/article/${it.id}"))
                .body(it)
        }
    }

    @PatchMapping("/{id}", consumes = ["multipart/form-data"])
    @Operation(summary = "게시글 수정 API")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("hasRole('ROLE_ADMIN') or @articleChecker.isEditable(#id, T(java.time.Instant).now())")
    fun updateArticle(
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
        @PathVariable @Parameter(description = "게시글 ID") id: Long,
        @Valid @ModelAttribute articleForm: ArticleForm,
    ): ResponseEntity<ArticleDetailResponse> {
        return ResponseEntity.ok(articleService.updateArticle(id, articleForm))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제 API")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("hasRole('ROLE_ADMIN') or @articleChecker.isAuthor(#id)")
    fun deleteArticle(
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
        @PathVariable @Parameter(description = "게시글 ID") id: Long,
    ): ResponseEntity<Unit> {
        articleService.deleteArticle(id)
        return ResponseEntity.noContent().build()
    }
}
