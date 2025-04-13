package org.project.portfolio.user.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.project.portfolio.common.domain.AuthenticatedUser
import org.project.portfolio.common.presentation.response.ApiResponse
import org.project.portfolio.config.SwaggerConfig.Companion.USER_API_TAG
import org.project.portfolio.user.application.UserManageService
import org.project.portfolio.user.presentation.response.UserResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): ApiResponse<UserResponse> {
        return ApiResponse.success(
            UserResponse.from(userService.getUser(authenticatedUser.email)),
        )
    }

    /** 회원 탈퇴 기능 */
    @DeleteMapping
    @Operation(summary = "회원 탈퇴 API")
    fun deleteUser(
        @AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
    ): ApiResponse<Unit> {
        userService.deleteUser(authenticatedUser.userId)
        return ApiResponse.success(Unit)
    }
}
