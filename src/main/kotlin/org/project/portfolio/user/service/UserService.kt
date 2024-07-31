package org.project.portfolio.user.service

import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.exception_handler.ErrorCode
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    /**
     * Get user information
     * @param email 유저 이메일
     */
    fun getUser(email: String): User {
        return userRepository.findById(email).orElseThrow {
            throw BusinessException(ErrorCode.USER_NOT_FOUND)
        }
    }

    /**
     * Delete user
     * @param name 유저 이름
     */
    fun deleteUser(name: String) {
        userRepository.deleteById(name)
    }
}
