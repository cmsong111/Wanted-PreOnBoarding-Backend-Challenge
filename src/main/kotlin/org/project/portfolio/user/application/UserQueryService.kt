package org.project.portfolio.user.application

import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserQueryService(
    private val userRepository: UserRepository,
) {
    fun getUser(email: String): User {
        return userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))
    }

    fun getUser(userId: Long): User {
        return userRepository.findByIdOrNull(userId)
            ?: throw NotFoundException(User::class.java, mapOf("userId" to userId))
    }
}
