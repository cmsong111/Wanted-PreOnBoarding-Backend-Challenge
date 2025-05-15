package org.project.portfolio.user.presentation.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.user.domain.User

@Schema(description = "User 헤더 Response")
data class UserSummaryResponse(
    var id: Long? = null,
    @field:Schema(description = "유저 이메일(PK)")
    val email: String,
    @field:Schema(description = "유저 이름")
    val name: String,
    @field:Schema(description = "유저 프로필 이미지")
    val profileImage: String?,
) {
    companion object {
        fun from(user: User?): UserSummaryResponse {
            return user?.let {
                UserSummaryResponse(
                    id = it.id,
                    email = it.email,
                    name = it.name,
                    profileImage = it.profileImage,
                )
            } ?: UserSummaryResponse(
                email = "탈퇴한 유저",
                name = "탈퇴한 유저",
                profileImage = null,
            )
        }
    }
}
