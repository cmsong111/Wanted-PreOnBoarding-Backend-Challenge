package org.project.portfolio.writing.tone

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.converter.BeanOutputConverter
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.stereotype.Component

@Component
class OllamaWritingToneGenerator(
    private val chatModel: OllamaChatModel,
) : WritingToneGenerator {

    override fun generate(
        articleWritingForm: ArticleWritingForm,
        toneDescription: String,
        tokenLimit: Int,
    ): ArticleWritingForm {
        val outputConverter: BeanOutputConverter<ArticleWritingForm> = BeanOutputConverter(ArticleWritingForm::class.java)

        val assistantMessage = AssistantMessage(
            """
                당신은 글의 말투나 문체를 다른 스타일로 자연스럽게 바꿔주는 글쓰기 스타일 변환 전문가입니다.

                아래 사용자로부터 전달되는 글은 특정 말투(style description)를 기반으로 새롭게 작성되어야 합니다.

                스타일 설명에는 어휘 선택, 문장 길이, 어조(예: 친절한, 무뚝뚝한), 문법적 특징, 형식적/비형식적 여부 등이 포함되어 있습니다.

                다음 지침을 따르세요:

                - 제목(title)은 255자 이내로 작성하세요.
                - 내용(description)은 2000자 이내로 작성하세요.
                - 단, 내용은 원문보다 **짧게 요약하지 말고**, 의미를 풍부하게 유지하거나 필요시 확장해서 작성하세요.
                - 표현을 풍부하게 하고 허풍, 감정, 상황 묘사를 추가해도 좋습니다.
                - 주어진 스타일 설명을 충분히 반영하여 자연스럽고 일관된 말투로 작성하세요.
                - 작성 결과는 JSON 형식으로 출력되며, 아래 스키마에 맞춰야 합니다.
                - 오직 결과 데이터만 출력하세요. 설명이나 코멘트는 포함하지 마세요.

                이제 사용자로부터 글이 전달됩니다.
                """.trimIndent(),
        )
        val userMessage = UserMessage(
            """
            다음은 사용자가 작성한 글입니다. 이 글을 $toneDescription 스타일로 변환해주세요:

            --- title
            ${articleWritingForm.title}
            --- content
            ${articleWritingForm.content}
            ---
            """.trimIndent(),
        )

        val response: ChatResponse = chatModel.call(
            Prompt(
                listOf<Message>(
                    assistantMessage,
                    userMessage,
                ),
                OllamaOptions.builder()
                    .model("gemma3:latest")
                    .format(outputConverter.jsonSchemaMap)
                    .temperature(0.9)
                    .topP(0.95)
                    .numPredict(2048)
                    .build(),
            ),
        )

        logger.info { "Response: $response" }

        return outputConverter.convert(response.result.output.text!!)
            ?: throw IllegalArgumentException("Response is null")
    }

    companion object {
        private val logger: KLogger = KotlinLogging.logger {}
    }
}
