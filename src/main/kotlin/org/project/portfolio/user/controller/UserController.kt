package org.project.portfolio.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** User API 컨트롤러 */
@Tag(name = "2. User", description = "The user API")
@SecurityRequirement(name = "Bearer Authentication")
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService,
) {
    /**
     * 내 정보 조회 API
     * @param principal 로그인 정보
     * @return 사용자 정보
     */
    @GetMapping
    @Operation(summary = "내 정보 조회 API")
    fun getUserInfo(
        @AuthenticationPrincipal userDetails: UserDetails,
    ): ResponseEntity<User> {
        return ResponseEntity.ok(userService.getUser(userDetails.username))
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
