package org.project.portfolio.writing.tone.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WritingToneRepository : JpaRepository<WritingTone, Long> {
    /** 사용자 ID로 말투 분석 이력 조회 */
    fun findByUserId(
        userId: Long,
        pageable: Pageable,
    ): Page<WritingTone>
}
