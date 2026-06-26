package org.project.portfolio.article.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.project.portfolio.article.application.CommentCommandService
import org.project.portfolio.article.application.CommentQueryService
import org.project.portfolio.article.presentation.request.CommentForm
import org.project.portfolio.article.presentation.request.CommentUpdateForm
import org.project.portfolio.article.presentation.response.CommentResponse
import org.project.portfolio.config.SwaggerConfig.Companion.BEARER_AUTH
import org.project.portfolio.config.SwaggerConfig.Companion.COMMENT_API_TAG
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/v1/articles/{articleId}/comments")
@Tag(name = COMMENT_API_TAG, description = "API for managing comments")
class CommentController(
    private val commentQueryService: CommentQueryService,
    private val commentCommandService: CommentCommandService,
) {
    @GetMapping
    @Operation(summary = "게시글 댓글 페이징 조회 API")
    fun getComments(
        @PathVariable articleId: Long,
        @PageableDefault(page = 0, size = 20, sort = ["createdAt"], direction = Sort.Direction.ASC)
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<PagedModel<CommentResponse>> {
        return ResponseEntity.ok(
            PagedModel(
                commentQueryService.getComments(
                    articleId = articleId,
                    pageable = pageable,
                ),
            ),
        )
    }

    @PostMapping
    @Operation(summary = "Create a new comment for a specific article")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    fun createComment(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable articleId: Long,
        @Valid @RequestBody commentForm: CommentForm,
    ): ResponseEntity<CommentResponse> {
        val comment = commentCommandService.createComment(
            email = jwt.subject,
            articleId = articleId,
            content = commentForm.content,
        )
        return ResponseEntity.created(URI.create("/api/v1/articles/$articleId/comments/${comment.id}")).body(comment)
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "Update an existing comment")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @commentValidator.isAuthor(#articleId, #commentId, authentication.name))")
    fun updateComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @Valid @RequestBody commentUpdateForm: CommentUpdateForm,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<CommentResponse> {
        return ResponseEntity.ok(
            commentCommandService.updateComment(
                email = jwt.subject,
                articleId = articleId,
                commentId = commentId,
                content = commentUpdateForm.content,
            ),
        )
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ROLE_ADMIN') or @commentValidator.isAuthor(#articleId, #commentId, authentication.name))")
    @Operation(summary = "Delete an existing comment")
    @SecurityRequirement(name = BEARER_AUTH)
    fun deleteComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Unit> {
        commentCommandService.deleteComment(
            commentId = commentId,
        )
        return ResponseEntity.noContent().build()
    }
}
