package org.project.portfolio.user.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.project.portfolio.config.SwaggerConfig.Companion.USER_API_TAG
import org.project.portfolio.user.application.UserManageService
import org.project.portfolio.user.presentation.response.UserResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** User API 컨트롤러 */
@Tag(name = USER_API_TAG, description = "The user API")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserManageService,
) {
    /**
     * 내 정보 조회 API
     * @param authenticatedUser 로그인 정보
     * @return 사용자 정보
     */
    @GetMapping
    @Operation(summary = "내 정보 조회 API")
    fun getUserInfo(
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity.ok(
            UserResponse.from(
                user = userService.getUser(
                    email = userDetails.username,
                ),
            ),
        )
    }

    /** 회원 탈퇴 기능 */
    @DeleteMapping
    @Operation(summary = "회원 탈퇴 API")
    fun deleteUser(
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<Unit> {
        userService.deleteUser(userDetails.username)
        return ResponseEntity.noContent().build()
    }
}
