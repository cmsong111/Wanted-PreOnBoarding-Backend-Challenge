package org.project.portfolio.writing.tone

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.Instant

@Entity
class WritingTone(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    val userId: Long,

    @Column(nullable = false)
    var isPublic: Boolean = false,

    /** 말투 설명 */
    @Column(columnDefinition = "LONGTEXT")
    var description: String,

    @Column(updatable = false)
    val createdAt: Instant = Instant.now(),
)
