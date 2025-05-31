package org.project.portfolio.writing.tone

interface WritingToneGenerator {
    fun generate(
        articleWritingForm: ArticleWritingForm,
        toneDescription: String,
        tokenLimit: Int = 2000,
    ): ArticleWritingForm
}
