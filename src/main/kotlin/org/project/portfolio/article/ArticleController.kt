package org.project.portfolio.article

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.project.portfolio.article.dto.ArticleRequest
import org.project.portfolio.article.entity.Article
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/article")
@Tag(name = "3. Article", description = "The article API")
class ArticleController(
    private val articleService: ArticleService
) {

    @GetMapping
    @Operation(summary = "게시글 조회 API")
    fun getArticles(): ResponseEntity<List<Article>> {
        return ResponseEntity.ok(articleService.getArticles())
    }

    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회 API")
    fun getArticle(
        @PathVariable @Parameter(description = "게시글 ID") id: Long
    ): ResponseEntity<Article> {
        return ResponseEntity.ok(articleService.getArticle(id))
    }

    @PostMapping
    @Operation(summary = "게시글 생성 API")
    @SecurityRequirement(name = "Bearer Authentication")
    fun createArticle(
        principal: Principal,
        @Valid @RequestBody @Parameter(description = "게시글 요청 폼") articleRequest: ArticleRequest
    ): ResponseEntity<Article> {
        return ResponseEntity.ok(articleService.createArticle(principal.name, articleRequest))
    }

    @PatchMapping("/{id}")
    @Operation(summary = "게시글 수정 API")
    @SecurityRequirement(name = "Bearer Authentication")
    fun updateArticle(
        principal: Principal,
        @PathVariable @Parameter(description = "게시글 ID") id: Long,
        @Valid @RequestBody @Parameter(description = "게시글 요청 폼") articleRequest: ArticleRequest
    ): ResponseEntity<Article> {
        return ResponseEntity.ok(articleService.updateArticle(principal.name, id, articleRequest))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제 API")
    @SecurityRequirement(name = "Bearer Authentication")
    fun deleteArticle(
        principal: Principal,
        @PathVariable @Parameter(description = "게시글 ID") id: Long
    ): ResponseEntity<Unit> {
        articleService.deleteArticle(principal.name, id)
        return ResponseEntity.noContent().build()
    }

}
