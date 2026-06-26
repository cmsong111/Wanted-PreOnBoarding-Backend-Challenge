package org.project.portfolio.writing.tone.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.project.portfolio.common.domain.BaseTimeEntity

@Entity
class WritingTone(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val userId: Long,

    @Column(nullable = false)
    var isPublic: Boolean = false,

    /** 말투 설명 */
    @Column(columnDefinition = "TEXT")
    var description: String,
) : BaseTimeEntity()
