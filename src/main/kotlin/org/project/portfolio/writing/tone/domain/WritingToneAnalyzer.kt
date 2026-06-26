package org.project.portfolio.writing.tone.domain

interface WritingToneAnalyzer {
    fun analyze(text: List<String>): String
}
