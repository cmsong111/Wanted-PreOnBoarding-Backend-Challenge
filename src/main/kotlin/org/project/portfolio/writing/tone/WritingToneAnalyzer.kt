package org.project.portfolio.writing.tone

interface WritingToneAnalyzer {
    fun analyze(
        text: List<String>,
    ): String
}
