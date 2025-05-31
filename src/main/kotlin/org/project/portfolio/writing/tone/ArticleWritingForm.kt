package org.project.portfolio.writing.tone

import com.fasterxml.jackson.annotation.JsonProperty


/**
 * @param title 제목
 * @param content 내용
 */
data class ArticleWritingForm(
    @field:JsonProperty(required = true, value = "title")
    val title: String,
    @field:JsonProperty(required = true, value = "description")
    val content: String,
)
