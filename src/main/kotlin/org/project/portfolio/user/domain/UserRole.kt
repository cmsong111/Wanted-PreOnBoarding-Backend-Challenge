package org.project.portfolio.user.domain

import org.springframework.security.core.GrantedAuthority

/** The roles that a user can have. */
enum class UserRole : GrantedAuthority {
    /** A regular user. */
    USER,

    /** An administrator. */
    ADMIN,
    ;

    /** Returns the authority of this role. */
    override fun getAuthority(): String {
        return "$ROLE_PREFIX$name"
    }

    companion object {
        const val ROLE_PREFIX = "ROLE_"

        fun parse(authority: String): UserRole {
            return valueOf(authority.trim().removePrefix(ROLE_PREFIX))
        }

        fun convert(authorities: List<String>): Set<UserRole> {
            return authorities.map { parse(it) }.toSet()
        }
    }
}
