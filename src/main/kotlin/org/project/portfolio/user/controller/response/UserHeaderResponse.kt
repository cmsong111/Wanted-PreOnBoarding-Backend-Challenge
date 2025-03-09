package org.project.portfolio.user.controller.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.user.entity.User

@Schema(description = "User 헤더 Response")
data class UserHeaderResponse(
    @field:Schema(description = "유저 이메일(PK)")
    val email: String,
    @field:Schema(description = "유저 이름")
    val name: String,
    @field:Schema(description = "유저 프로필 이미지")
    val profileImage: String?,
) {
    companion object {
        fun from(user: User) = UserHeaderResponse(
            email = user.email,
            name = user.name,
            profileImage = user.profileImage
        )
    }
}
