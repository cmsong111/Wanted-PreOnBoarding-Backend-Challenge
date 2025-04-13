package org.project.portfolio.user.domain.exception

import org.project.portfolio.common.domain.exception.NotFoundException

data class UserNotFoundException(
    override val message: String = "User not found",
) : NotFoundException(message = message)
