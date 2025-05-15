package org.project.portfolio.auth.domain

import org.project.portfolio.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * JWT용 UserDetails 구현체
 * @param email 사용자 이메일
 * @param authorities 권한 목록
 */
class JwtUserDetails(
    private val email: String,
    private val authorities: Collection<GrantedAuthority>,
) : UserDetails {
    override fun getUsername(): String = email

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String? = null

    companion object {
        fun from(userDetails: UserDetails): JwtUserDetails {
            return JwtUserDetails(
                email = userDetails.username,
                authorities = userDetails.authorities,
            )
        }

        fun from(user: User): JwtUserDetails {
            return JwtUserDetails(
                email = user.email,
                authorities = user.roles,
            )
        }
    }
}
