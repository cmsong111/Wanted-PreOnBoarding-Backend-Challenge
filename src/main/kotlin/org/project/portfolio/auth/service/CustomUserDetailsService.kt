package org.project.portfolio.auth.service

import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {
    /**
     * 스프링 시큐리티에서 사용되는 사용자명으로 정보 조회하는 메소드
     * @param username 사용자명
     * @return 사용자 정보
     */
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User = userRepository.findByEmail(username)
            ?: throw IllegalArgumentException("User not found with email: $username")

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.email)
            .password(user.password)
            .authorities(user.roles)
            .build()
    }
}
