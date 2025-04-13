package org.project.portfolio.user.presentation.response

import io.swagger.v3.oas.annotations.media.Schema
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRole
import java.time.Instant

@Schema(description = "User Response")
data class UserResponse(
    @field:Schema(description = "유저 ID")
    val id: Long,
    @field:Schema(description = "유저 이메일")
    var email: String,
    @field:Schema(description = "유저 이름")
    var name: String,
    @field:Schema(description = "유저 전화번호")
    var phone: String,
    @field:Schema(description = "유저 프로필 이미지")
    var profileImage: String? = null,
    @field:Schema(description = "유저 권한")
    var roles: MutableSet<UserRole>,
    @field:Schema(description = "유저 생성일시")
    var createdAt: Instant,
    @field:Schema(description = "유저 수정일시")
    var updatedAt: Instant,
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                email = user.email,
                name = user.name,
                phone = user.phone,
                profileImage = user.profileImage,
                roles = user.roles,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
            )
        }
    }
}
