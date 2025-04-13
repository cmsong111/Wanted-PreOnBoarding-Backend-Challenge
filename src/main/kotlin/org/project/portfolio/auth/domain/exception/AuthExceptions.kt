@file:Suppress("ktlint:standard:filename")

package org.project.portfolio.auth.domain.exception

import org.project.portfolio.common.domain.exception.BadRequestException
import org.project.portfolio.common.domain.exception.ConflictException

data class LoginFailedException(
    override val message: String = "로그인을 실패했습니다",
) : BadRequestException(message = message)

data class AlreadyExistingEmailException(
    override val message: String = "이미 존재하는 이메일입니다",
) : ConflictException(message = message)
