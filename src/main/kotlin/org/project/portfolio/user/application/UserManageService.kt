package org.project.portfolio.user.application

import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.exception.UserNotFoundException
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
            ?: throw UserNotFoundException()
    }

    /**
     * Delete user
     * @param userId 유저 아이디
     */
    @Transactional
    fun deleteUser(userId: Long) {
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw UserNotFoundException()

        user.email = "${user.email}_deleted_${System.currentTimeMillis()}"
        userRepository.deleteById(userId)
    }
}
