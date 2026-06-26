package org.project.portfolio.user.application

import org.project.portfolio.common.domain.exception.BadRequestException
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserCommandService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    /**
     * 비밀번호 변경
     */
    fun changePassword(
        email: String,
        currentPassword: String,
        newPassword: String,
    ) {
        val user: User = userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))

        if (!passwordEncoder.matches(currentPassword, user.password)) {
            throw BadRequestException("현재 비밀번호가 일치하지 않습니다.")
        }

        user.changePassword(passwordEncoder.encode(newPassword))
    }

    /**
     * 회원 탈퇴 (Soft Delete + 이메일 재가입 가능 처리)
     */
    fun deleteUser(email: String) {
        val user: User = userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))

        user.email = "${user.email}.deleted.${System.currentTimeMillis()}"
        userRepository.delete(user)
    }
}
