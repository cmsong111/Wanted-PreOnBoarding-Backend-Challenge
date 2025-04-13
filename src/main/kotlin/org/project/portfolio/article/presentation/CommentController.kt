package org.project.portfolio.article.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.project.portfolio.article.application.CommentService
import org.project.portfolio.article.presentation.request.CommentForm
import org.project.portfolio.article.presentation.response.CommentResponse
import org.project.portfolio.common.domain.AuthenticatedUser
import org.project.portfolio.config.SwaggerConfig.Companion.BEARER_AUTH
import org.project.portfolio.config.SwaggerConfig.Companion.COMMENT_API_TAG
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
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
    private val commentService: CommentService,
) {
    @PostMapping
    @Operation(summary = "Create a new comment for a specific article")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    fun createComment(
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
        @PathVariable articleId: Long,
        @Valid @RequestBody commentForm: CommentForm,
    ): ResponseEntity<CommentResponse> {
        val comment = commentService.createComment(
            userId = authenticatedUser.userId,
            articleId = articleId,
            content = commentForm.content,
        )
        return ResponseEntity.created(URI.create("/api/v1/articles/$articleId/comments/${comment.id}")).body(comment)
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "Update an existing comment")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("isAuthenticated() and @commentValidator.isAuthor(#articleId, #commentId, #authenticatedUser.userId)")
    fun updateComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @Valid @RequestBody commentForm: CommentForm,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<CommentResponse> {
        return ResponseEntity.ok(
            commentService.updateComment(
                userId = authenticatedUser.userId,
                articleId = articleId,
                commentId = commentId,
                content = commentForm.content,
            ),
        )
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated() and @commentValidator.isAuthor(#articleId, #commentId, #authenticatedUser.userId)")
    @Operation(summary = "Delete an existing comment")
    @SecurityRequirement(name = BEARER_AUTH)
    fun deleteComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): ResponseEntity<Unit> {
        commentService.deleteComment(
            commentId = commentId,
        )
        return ResponseEntity.noContent().build()
    }
}
