package org.project.portfolio.writing.tone

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.project.portfolio.config.SwaggerConfig.Companion.BEARER_AUTH
import org.springframework.data.web.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/v1/speech")
@Tag(name = "Speech Analysis", description = "Speech Analysis API")
class WritingToneController(
    private val writingToneService: WritingToneService,
) {
    @PostMapping
    @Operation(summary = "Analyze speech data")
    @SecurityRequirement(name = BEARER_AUTH)
    fun analyzeSpeech(
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<WritingTone> {
        val email = userDetails.username
        val result = writingToneService.analyzeSpeech(email)
        return ResponseEntity.ok(result)
    }

    @GetMapping
    @Operation(summary = "Get speech data by ID")
    @SecurityRequirement(name = BEARER_AUTH)
    fun getSpeechById(
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<PagedModel<WritingTone>> {
        return ResponseEntity.ok(
            PagedModel(
                writingToneService.getSpeeches(userDetails.username),
            ),
        )
    }

    @PostMapping("/gnerate")
    @SecurityRequirement(name = BEARER_AUTH)
    fun generateSpeech(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody articleWritingForm: ArticleWritingForm,
    ): ResponseEntity<ArticleWritingForm> {
        val result = writingToneService.createSpeech(
            email = userDetails.username,
            articleWritingForm = articleWritingForm,
        )
        return ResponseEntity.ok(result)
    }
}
