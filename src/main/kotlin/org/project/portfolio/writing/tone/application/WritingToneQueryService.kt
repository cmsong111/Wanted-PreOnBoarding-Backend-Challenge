package org.project.portfolio.writing.tone.application

import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.writing.tone.domain.WritingToneRepository
import org.project.portfolio.writing.tone.presentation.response.WritingToneResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class WritingToneQueryService(
    private val userRepository: UserRepository,
    private val writingToneRepository: WritingToneRepository,
) {
    /** 유저의 말투 분석 이력 조회 */
    fun getWritingTones(
        email: String,
        pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
    ): Page<WritingToneResponse> {
        val user = userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))

        return writingToneRepository.findByUserId(
            userId = user.id,
            pageable = pageable,
        ).map(WritingToneResponse::from)
    }
}
