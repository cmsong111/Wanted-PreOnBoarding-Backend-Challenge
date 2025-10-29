package org.project.portfolio.writing.tone

import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.ai.chat.prompt.Prompt
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.stereotype.Component

@Component
class OllamaWritingToneAnalyzer(
    private val chatModel: OllamaChatModel,
) : WritingToneAnalyzer {
    override fun analyze(text: List<String>): String {
        // 어시스턴트 메시지
        val assistantMessage = AssistantMessage(
            """
                당신은 글쓰기 스타일 분석에 능한 전문가입니다.

                당신의 임무는 사용자가 작성한 여러 문장을 바탕으로,
                해당 사용자의 독특한 말투와 글쓰기 스타일의 특징을 분석해내는 것입니다.

                분석 내용은 다음을 포함하되, 이에 국한되지 않습니다:

                - 단어 선택과 어휘 사용 경향
                - 문장의 구조와 복잡도
                - 말투의 격식 수준 (예: 공손, 캐주얼, 격식체 등)
                - 이모티콘, 이모지, 인터넷 은어의 사용 여부
                - 감정적 톤 (예: 명랑함, 진지함, 비꼼 등)
                - 문장의 시작/종결 패턴
                - 자주 사용하는 표현이나 말버릇
                - 구두점이나 서식의 사용 방식
                - 지역적 또는 문화적인 언어 특징
                - 전반적인 인상이나 성격적 요소

                분석 결과는 마크다운 형식으로 제목과 목록을 활용해 구조화해서 작성해주세요.

                목표는 추후 AI가 이 말투를 효과적으로 모방할 수 있도록 충분한 정보를 제공하는 것입니다.
            """.trimIndent(),
        )

        val userMessage = UserMessage(
            """
                다음은 한 사용자가 작성한 글입니다. 이 사용자의 말투와 글쓰기 스타일의 특징을 분석해주세요:

                ${text.joinToString(separator = "\n")}
            """.trimIndent(),
        )

        return chatModel.call(
            Prompt(
                listOf<Message>(
                    assistantMessage,
                    userMessage,
                ),
                OllamaOptions.builder()
                    .model("gemma3:latest")
                    .temperature(0.7)
                    .topP(0.9)
                    .presencePenalty(0.6)
                    .frequencyPenalty(0.3)
                    .build(),
            ),
        ).result.output.text
            ?: throw IllegalStateException("Failed to analyze speech")
    }
}
