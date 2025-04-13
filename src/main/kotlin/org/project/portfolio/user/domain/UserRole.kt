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
        return "ROLE_$name"
    }
}
