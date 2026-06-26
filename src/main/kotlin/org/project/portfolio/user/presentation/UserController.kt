package org.project.portfolio.user.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.project.portfolio.config.SwaggerConfig.Companion.BEARER_AUTH
import org.project.portfolio.config.SwaggerConfig.Companion.USER_API_TAG
import org.project.portfolio.user.application.UserCommandService
import org.project.portfolio.user.application.UserQueryService
import org.project.portfolio.user.presentation.request.PasswordChangeRequest
import org.project.portfolio.user.presentation.response.UserPublicResponse
import org.project.portfolio.user.presentation.response.UserResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** User API 컨트롤러 */
@Tag(name = USER_API_TAG, description = "The user API")
@RestController
@RequestMapping("/api/v1")
class UserController(
    private val userQueryService: UserQueryService,
    private val userCommandService: UserCommandService,
) {
    /** 내 정보 조회 API */
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = BEARER_AUTH)
    @Operation(summary = "내 정보 조회 API")
    fun getUserInfo(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(
            UserResponse.from(
                user = userQueryService.getUser(email = jwt.subject),
            ),
        )
    }

    /** 공개 프로필 조회 API */
    @GetMapping("/users/{userId}")
    @Operation(summary = "사용자 공개 프로필 조회 API")
    fun getUserPublicProfile(
        @PathVariable userId: Long,
    ): ResponseEntity<UserPublicResponse> {
        return ResponseEntity.ok(
            UserPublicResponse.from(
                user = userQueryService.getUser(userId = userId),
            ),
        )
    }

    /** 비밀번호 변경 API */
    @PatchMapping("/user/password")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = BEARER_AUTH)
    @Operation(summary = "비밀번호 변경 API")
    fun changePassword(
        @AuthenticationPrincipal jwt: Jwt,
        @Valid @RequestBody request: PasswordChangeRequest,
    ): ResponseEntity<Unit> {
        userCommandService.changePassword(
            email = jwt.subject,
            currentPassword = request.currentPassword,
            newPassword = request.newPassword,
        )
        return ResponseEntity.noContent().build()
    }

    /** 회원 탈퇴 API */
    @DeleteMapping("/user")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = BEARER_AUTH)
    @Operation(summary = "회원 탈퇴 API")
    fun deleteUser(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<Unit> {
        userCommandService.deleteUser(jwt.subject)
        return ResponseEntity.noContent().build()
    }
}
