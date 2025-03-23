package org.project.portfolio.user.entity

import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.project.portfolio.common.entity.BaseEntity

/** 유저 엔티티 */
@Entity
@Table(name = "users")
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    /** 유저 이메일 */
    @Column(unique = true)
    var email: String,
    /** 유저 이름 */
    var name: String,
    /** 유저 핸드폰 번호 */
    var phone: String,
    /** 유저 프로필 이미지 */
    var profileImage: String? = null,
    /** 유저 비밀번호 */
    var password: String,
    /** 유저 역할(권한) */
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    var roles: MutableSet<UserRole> = mutableSetOf(UserRole.USER),
) : BaseEntity() {
    companion object {
        /** 유저 생성 */
        fun create(
            email: String,
            name: String,
            phone: String,
            password: String,
        ): User {
            return User(
                email = email,
                name = name,
                phone = phone,
                password = password,
                profileImage = "https://picsum.photos/id/100/200/200",
            )
        }

        /** 탈퇴한 유저 생성 */
        fun createWithdrawnUser(): User {
            return User(
                email = "empty",
                name = "탈퇴한 유저",
                phone = "000-0000-0000",
                password = "empty",
            )
        }
    }

    /** 계정 역할 부여  */
    fun addRole(role: UserRole) {
        roles.add(role)
    }
}
