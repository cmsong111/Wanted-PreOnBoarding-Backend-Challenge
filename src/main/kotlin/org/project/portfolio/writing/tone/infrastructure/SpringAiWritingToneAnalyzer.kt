package org.project.portfolio.writing.tone.infrastructure

import org.project.portfolio.writing.tone.domain.WritingToneAnalyzer
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component

@Component
class SpringAiWritingToneAnalyzer(
    chatClientBuilder: ChatClient.Builder,
    @Value("classpath:prompts/writing-tone-analyze.md")
    systemPromptResource: Resource,
) : WritingToneAnalyzer {
    private val chatClient: ChatClient = chatClientBuilder
        .defaultSystem(systemPromptResource)
        .defaultOptions(
            ChatOptions.builder()
                .temperature(0.7)
                .topP(0.9)
                .build(),
        )
        .build()

    override fun analyze(text: List<String>): String {
        return chatClient.prompt()
            .user(
                """
                다음은 한 사용자가 작성한 글입니다. 이 사용자의 말투와 글쓰기 스타일의 특징을 분석해주세요:

                ${text.joinToString(separator = "\n")}
                """.trimIndent(),
            )
            .call()
            .content()
            ?: throw IllegalStateException("Failed to analyze writing tone")
    }
}
