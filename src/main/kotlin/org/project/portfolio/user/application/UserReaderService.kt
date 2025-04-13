package org.project.portfolio.user.application

import org.project.portfolio.user.domain.UserRepository
import org.springframework.stereotype.Service

@Service
class UserReaderService(
    private val userRepository: UserRepository,
)
