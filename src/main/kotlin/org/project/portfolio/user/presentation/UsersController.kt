package org.project.portfolio.user.presentation

import io.swagger.v3.oas.annotations.tags.Tag
import org.project.portfolio.config.SwaggerConfig.Companion.USERS_API_TAG
import org.project.portfolio.user.application.UserManageService
import org.project.portfolio.user.presentation.response.UserSummaryResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** User API 컨트롤러 */
@Tag(name = USERS_API_TAG, description = "The users API")
@RestController
@RequestMapping("/api/v1/users")
class UsersController(
    private val userService: UserManageService,
) {
    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId: Long,
    ): ResponseEntity<UserSummaryResponse> {
        return ResponseEntity.ok(
            UserSummaryResponse.from(userService.getUser(userId = userId)),
        )
    }
}
