package org.project.portfolio.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.ServletRequest
import jakarta.validation.Valid
import org.project.portfolio.article.dto.ArticleRequest
import org.project.portfolio.article.dto.ArticleResponseDetail
import org.project.portfolio.article.dto.ArticleResponseHeader
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.service.ArticleService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.security.Principal

/**
 * 게시글 컨트롤러
 * <p>스프링 시큐리티에서 @GET 요청에 한해 permitAll 적용</p>
 */
@RestController
@RequestMapping("/api/v1/article")
@Tag(name = "3. Article", description = "The article API")
class ArticleController(
    private val articleService: ArticleService
) {

    @GetMapping
    @Operation(summary = "게시글 조회 API")
    fun getArticles(
        @RequestParam(required = false, defaultValue = "1") page: Int,
        @RequestParam(required = false, defaultValue = "10") size: Int,
        @RequestParam(required = false, defaultValue = "createdAt") sort: String,
        @RequestParam(required = false) title: String?
    ): ResponseEntity<List<ArticleResponseHeader>> {
        return ResponseEntity.ok(
            articleService.getArticles(
                pageable = PageRequest.of(page - 1, size, Sort.Direction.DESC, sort),
                title = title,
            )
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회 API")
    fun getArticle(
        @PathVariable @Parameter(description = "게시글 ID") id: Long,
        servletRequest: ServletRequest
    ): ResponseEntity<ArticleResponseDetail> {
        return ResponseEntity.ok(articleService.getArticle(id, servletRequest.remoteAddr))
    }

    @PostMapping
    @Operation(summary = "게시글 생성 API")
    @SecurityRequirement(name = "Bearer Authentication")
    fun createArticle(
        principal: Principal,
        @Valid @RequestBody @Parameter(description = "게시글 요청 폼") articleRequest: ArticleRequest
    ): ResponseEntity<Article> {
        val article: Article = articleService.createArticle(principal.name, articleRequest)
        return ResponseEntity
            .created(URI.create("/api/v1/article/${article.id}"))
            .body(article)
    }

    @PatchMapping("/{id}")
    @Operation(summary = "게시글 수정 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @articleChecker.isEditable(#id)")
    fun updateArticle(
        principal: Principal,
        @PathVariable @Parameter(description = "게시글 ID") id: Long,
        @Valid @RequestBody @Parameter(description = "게시글 요청 폼") articleRequest: ArticleRequest
    ): ResponseEntity<Article> {
        return ResponseEntity.ok(articleService.updateArticle(id, articleRequest))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제 API")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @articleChecker.isAuthor(#id)")
    fun deleteArticle(
        principal: Principal,
        @PathVariable @Parameter(description = "게시글 ID") id: Long
    ): ResponseEntity<Unit> {
        articleService.deleteArticle(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{id}/hard")
    @Operation(summary = "게시글 삭제 API", description = "게시글을 완전 삭제합니다. 복구가 불가능하니 주의하세요.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun hardDeleteArticle(
        principal: Principal,
        @PathVariable @Parameter(description = "게시글 ID") id: Long
    ): ResponseEntity<Unit> {
        articleService.hardDeleteArticle(id)
        return ResponseEntity.noContent().build()
    }

}
