package org.project.portfolio.user.presentation.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.user.domain.User
import java.time.Instant

@Schema(description = "User Response")
data class UserPublicResponse(
    @field:Schema(description = "유저 ID")
    val id: Long,
    @field:Schema(description = "유저 이메일")
    var email: String,
    @field:Schema(description = "유저 이름")
    var name: String,
    @field:Schema(description = "유저 프로필 이미지")
    var profileImage: String? = null,
    @field:Schema(description = "유저 생성일시")
    var createdAt: Instant,
) {
    companion object {
        fun from(user: User): UserPublicResponse {
            return UserPublicResponse(
                id = user.id,
                email = user.email,
                name = user.name,
                profileImage = user.profileImage,
                createdAt = user.createdAt,
            )
        }
    }
}
