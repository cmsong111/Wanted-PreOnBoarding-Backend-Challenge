package org.project.portfolio.user.service

import org.project.portfolio.common.exception.BusinessException
import org.project.portfolio.common.exception.ErrorCode
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    /**
     * Get user information
     * @param email 유저 이메일
     */
    @Transactional(readOnly = true)
    fun getUser(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)
    }

    /**
     * Delete user
     * @param userId 유저 아이디
     */
    @Transactional
    fun deleteUser(userId: Long) {
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        user.email = "${user.email}_deleted_${System.currentTimeMillis()}"
        userRepository.deleteById(userId)
    }
}
