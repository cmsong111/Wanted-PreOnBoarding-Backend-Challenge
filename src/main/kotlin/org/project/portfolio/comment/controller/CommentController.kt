package org.project.portfolio.comment.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.project.portfolio.comment.dto.CommentRequest
import org.project.portfolio.comment.dto.CommentResponse
import org.project.portfolio.comment.service.CommentService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.security.Principal

@RestController
@RequestMapping("/api/v1/article/{articleId}/comment")
@Tag(name = "4. Comments", description = "API for managing comments")
@SecurityRequirement(name = "Bearer Authentication")
class CommentController(
    private val commentService: CommentService
) {
    @PostMapping
    @Operation(summary = "Create a new comment for a specific article")
    fun createComment(
        principal: Principal,
        @PathVariable articleId: Long,
        @Valid @RequestBody commentRequest: CommentRequest
    ): ResponseEntity<CommentResponse> {
        println("create request method is called")
        val comment = commentService.createComment(principal.name, articleId, commentRequest)
        println("comment: $comment")
        return ResponseEntity.created(URI.create("/api/v1/articles/${articleId}/comments/${comment.id}")).body(comment)
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an existing comment")
    @PreAuthorize("@commentChecker.isAuthor(#id)")
    fun updateComment(
        principal: Principal,
        @PathVariable articleId: Long,
        @PathVariable id: Long,
        @Valid @RequestBody commentRequest: CommentRequest
    ): ResponseEntity<CommentResponse> {
        println("update request method is called")
        return ResponseEntity.ok(commentService.updateComment(id, commentRequest))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an existing comment")
    @PreAuthorize("@commentChecker.isAuthor(#id)")
    fun deleteComment(@PathVariable articleId: Long, @PathVariable id: Long): ResponseEntity<Unit> {
        commentService.deleteComment(id)
        return ResponseEntity.noContent().build()
    }
}
