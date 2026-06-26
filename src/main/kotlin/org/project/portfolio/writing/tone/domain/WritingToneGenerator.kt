package org.project.portfolio.writing.tone.domain

interface WritingToneGenerator {
    fun generate(
        draft: WritingToneDraft,
        toneDescription: String,
        tokenLimit: Int = 2000,
    ): WritingToneDraft
}
