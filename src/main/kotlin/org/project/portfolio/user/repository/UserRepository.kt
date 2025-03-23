package org.project.portfolio.user.repository

import org.project.portfolio.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/** 사용자 레포지토리 */
@Repository
interface UserRepository : JpaRepository<User, Long> {
    /** 이메일로 사용자 조회 */
    fun findByEmail(email: String): User?

    /** 이메일로 사용자 존재 여부 조회 */
    fun existsByEmail(email: String): Boolean
}
