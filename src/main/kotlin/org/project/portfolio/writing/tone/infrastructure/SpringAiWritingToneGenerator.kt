package org.project.portfolio.writing.tone.infrastructure

import org.project.portfolio.writing.tone.domain.WritingToneDraft
import org.project.portfolio.writing.tone.domain.WritingToneGenerator
import org.project.portfolio.writing.tone.presentation.request.ArticleWritingForm
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
class SpringAiWritingToneGenerator(
    chatClientBuilder: ChatClient.Builder,
    @Value("classpath:prompts/writing-tone-generate.md")
    systemPromptResource: Resource,
) : WritingToneGenerator {
    private val chatClient: ChatClient = chatClientBuilder
        .defaultSystem(systemPromptResource)
        .build()

    override fun generate(
        draft: WritingToneDraft,
        toneDescription: String,
        tokenLimit: Int,
    ): WritingToneDraft {
        val result = chatClient.prompt()
            .user(
                """
                다음은 사용자가 작성한 글입니다. 이 글을 아래 말투 스타일로 변환해주세요:

                --- style description
                $toneDescription
                --- title
                ${draft.title}
                --- content
                ${draft.content}
                ---
                """.trimIndent(),
            )
            .options(
                ChatOptions.builder()
                    .temperature(0.9)
                    .topP(0.95)
                    .maxTokens(tokenLimit)
                    .build(),
            )
            .call()
            .entity(ArticleWritingForm::class.java)
            ?: throw IllegalStateException("Failed to parse generated writing tone response")

        return WritingToneDraft(
            title = result.title,
            content = result.content,
        )
    }
}
