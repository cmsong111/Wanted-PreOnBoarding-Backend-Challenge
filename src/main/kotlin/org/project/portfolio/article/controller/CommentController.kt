package org.project.portfolio.article.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.net.URI
import java.security.Principal
import org.project.portfolio.article.controller.request.CommentForm
import org.project.portfolio.article.controller.response.CommentResponse
import org.project.portfolio.article.service.CommentService
import org.project.portfolio.config.SwaggerConfig.Companion.BEARER_AUTH
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/articles/{articleId}/comments")
@Tag(name = "4. Comments", description = "API for managing comments")
class CommentController(
    private val commentService: CommentService,
) {
    @PostMapping
    @Operation(summary = "Create a new comment for a specific article")
    @SecurityRequirement(name = BEARER_AUTH)
    fun createComment(
        principal: Principal,
        @PathVariable articleId: Long,
        @Valid @RequestBody commentForm: CommentForm,
    ): ResponseEntity<CommentResponse> {
        println("create request method is called")
        val comment = commentService.createComment(principal.name, articleId, commentForm.content!!)
        println("comment: $comment")
        return ResponseEntity.created(URI.create("/api/v1/articles/$articleId/comments/${comment.id}")).body(comment)
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "Update an existing comment")
    @SecurityRequirement(name = BEARER_AUTH)
    @PreAuthorize("@commentChecker.isAuthor(#id)")
    fun updateComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @Valid @RequestBody commentForm: CommentForm,
        @AuthenticationPrincipal principal: Principal,
    ): ResponseEntity<CommentResponse> {
        return ResponseEntity.ok(
            commentService.updateComment(
                email = principal.name,
                articleId = articleId,
                commentId = commentId,
                content = commentForm.content!!,
            ),
        )
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("@commentChecker.isAuthor(#commentId)")
    @Operation(summary = "Delete an existing comment")
    @SecurityRequirement(name = BEARER_AUTH)
    fun deleteComment(
        @PathVariable articleId: Long,
        @PathVariable commentId: Long,
        @AuthenticationPrincipal principal: Principal,
    ): ResponseEntity<Unit> {
        commentService.deleteComment(
            email = principal.name,
            articleId = articleId,
            commentId = commentId,
        )
        return ResponseEntity.noContent().build()
    }
}
