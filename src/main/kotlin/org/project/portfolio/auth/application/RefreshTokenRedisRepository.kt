package org.project.portfolio.auth.application

import org.project.portfolio.auth.domain.RefreshToken
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRedisRepository : CrudRepository<RefreshToken, String>
