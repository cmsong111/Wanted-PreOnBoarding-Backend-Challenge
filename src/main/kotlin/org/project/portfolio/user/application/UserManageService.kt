package org.project.portfolio.user.application

import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserManageService(
    private val userRepository: UserRepository,
) {
    /**
     * Get user information
     * @param email 유저 이메일
     */
    @Transactional(readOnly = true)
    fun getUser(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))
    }

    @Transactional(readOnly = true)
    fun getUser(userId: Long): User {
        return userRepository.findByIdOrNull(userId)
            ?: throw NotFoundException(User::class.java, mapOf("userId" to userId))
    }

    /**
     * Delete user
     * @param email 유저 아이디
     */
    @Transactional
    fun deleteUser(email: String) {
        val user: User = userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))

        user.email = "${user.email}.deleted.${System.currentTimeMillis()}"
        userRepository.deleteByEmail(user.email)
    }
}
