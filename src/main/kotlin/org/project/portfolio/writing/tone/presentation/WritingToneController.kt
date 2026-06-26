package org.project.portfolio.writing.tone.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.project.portfolio.config.SwaggerConfig.Companion.BEARER_AUTH
import org.project.portfolio.config.SwaggerConfig.Companion.WRITING_TONE_API_TAG
import org.project.portfolio.writing.tone.application.WritingToneCommandService
import org.project.portfolio.writing.tone.application.WritingToneQueryService
import org.project.portfolio.writing.tone.presentation.request.ArticleWritingForm
import org.project.portfolio.writing.tone.presentation.response.WritingToneResponse
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.data.web.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/writing-tones")
@Tag(name = WRITING_TONE_API_TAG, description = "Writing Tone Analysis & Generation API")
class WritingToneController(
    private val writingToneQueryService: WritingToneQueryService,
    private val writingToneCommandService: WritingToneCommandService,
) {
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내 말투 분석 API")
    @SecurityRequirement(name = BEARER_AUTH)
    fun analyzeWritingTone(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<WritingToneResponse> {
        return ResponseEntity.ok(writingToneCommandService.analyzeWritingTone(jwt.subject))
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내 말투 분석 이력 조회 API")
    @SecurityRequirement(name = BEARER_AUTH)
    fun getWritingTones(
        @AuthenticationPrincipal jwt: Jwt,
        @PageableDefault(page = 0, size = 10, sort = ["createdAt"], direction = Sort.Direction.DESC)
        @ParameterObject pageable: Pageable,
    ): ResponseEntity<PagedModel<WritingToneResponse>> {
        return ResponseEntity.ok(
            PagedModel(
                writingToneQueryService.getWritingTones(
                    email = jwt.subject,
                    pageable = pageable,
                ),
            ),
        )
    }

    @PostMapping("/generate")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "내 말투로 글 변환 API")
    @SecurityRequirement(name = BEARER_AUTH)
    fun generateWithWritingTone(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody articleWritingForm: ArticleWritingForm,
    ): ResponseEntity<ArticleWritingForm> {
        return ResponseEntity.ok(
            writingToneCommandService.generateWithWritingTone(
                email = jwt.subject,
                articleWritingForm = articleWritingForm,
            ),
        )
    }
}
