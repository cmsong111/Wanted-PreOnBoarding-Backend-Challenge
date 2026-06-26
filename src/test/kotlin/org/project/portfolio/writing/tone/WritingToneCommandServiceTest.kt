package org.project.portfolio.writing.tone

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.common.domain.exception.BadRequestException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.writing.tone.application.WritingToneCommandService
import org.project.portfolio.writing.tone.domain.WritingTone
import org.project.portfolio.writing.tone.domain.WritingToneAnalyzer
import org.project.portfolio.writing.tone.domain.WritingToneDraft
import org.project.portfolio.writing.tone.domain.WritingToneGenerator
import org.project.portfolio.writing.tone.domain.WritingToneRepository
import org.project.portfolio.writing.tone.presentation.request.ArticleWritingForm
import org.springframework.core.io.ClassPathResource
import org.springframework.data.domain.PageImpl
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate
import java.nio.charset.StandardCharsets

class WritingToneCommandServiceTest :
    DescribeSpec({
        val userRepository = mockk<UserRepository>()
        val articleRepository = mockk<ArticleRepository>()
        val commentRepository = mockk<CommentRepository>()
        val writingToneAnalyzer = mockk<WritingToneAnalyzer>()
        val writingToneGenerator = mockk<WritingToneGenerator>()
        val writingToneRepository = mockk<WritingToneRepository>()
        val transactionTemplate = mockk<TransactionTemplate>().apply {
            every { execute(any<TransactionCallback<Any>>()) } answers {
                firstArg<TransactionCallback<Any>>().doInTransaction(mockk(relaxed = true))
            }
        }

        val service = WritingToneCommandService(
            userRepository = userRepository,
            articleRepository = articleRepository,
            commentRepository = commentRepository,
            writingToneAnalyzer = writingToneAnalyzer,
            writingToneGenerator = writingToneGenerator,
            writingToneRepository = writingToneRepository,
            transactionTemplate = transactionTemplate,
        )

        describe("WritingTone .md 프롬프트 리소스") {
            it("writing-tone-analyze.md와 writing-tone-generate.md가 classpath에 존재한다") {
                val analyzePrompt = ClassPathResource("prompts/writing-tone-analyze.md")
                    .getContentAsString(StandardCharsets.UTF_8)
                val generatePrompt = ClassPathResource("prompts/writing-tone-generate.md")
                    .getContentAsString(StandardCharsets.UTF_8)

                analyzePrompt shouldContain "글쓰기 스타일 분석"
                generatePrompt shouldContain "스타일 변환 전문가"
            }
        }

        describe("WritingToneCommandService") {
            val user = User(
                id = 1L,
                email = "writer@test.com",
                name = "작성자",
                phone = "010-1234-5678",
                password = "encoded-password",
            )

            it("사용자의 게시글·댓글을 분석해 WritingToneResponse를 저장·반환한다") {
                every { userRepository.findByEmail(user.email) } returns user
                every { articleRepository.findByAuthorId(user.id, any()) } returns PageImpl(
                    listOf(Article.create(title = "제목", content = "내용", authorId = user.id)),
                )
                every { commentRepository.findByAuthorId(user.id, any()) } returns PageImpl(emptyList())
                every { writingToneAnalyzer.analyze(listOf("제목", "내용")) } returns "## 캐주얼한 존댓말"
                every { writingToneRepository.save(any()) } answers { firstArg() }

                val result = service.analyzeWritingTone(user.email)

                result.userId shouldBe user.id
                result.description shouldBe "## 캐주얼한 존댓말"
            }

            it("분석할 게시글·댓글이 없으면 BadRequestException이 발생한다") {
                every { userRepository.findByEmail(user.email) } returns user
                every { articleRepository.findByAuthorId(user.id, any()) } returns PageImpl(emptyList())
                every { commentRepository.findByAuthorId(user.id, any()) } returns PageImpl(emptyList())

                shouldThrow<BadRequestException> {
                    service.analyzeWritingTone(user.email)
                }
            }

            it("최근 말투로 글을 변환한다") {
                val input = ArticleWritingForm(title = "원본 제목", content = "원본 내용")
                val output = ArticleWritingForm(title = "변환 제목", content = "변환 내용")

                every { userRepository.findByEmail(user.email) } returns user
                every { writingToneRepository.findByUserId(user.id, any()) } returns PageImpl(
                    listOf(WritingTone(id = 10L, userId = user.id, description = "친절한 말투")),
                )
                every {
                    writingToneGenerator.generate(
                        WritingToneDraft(title = "원본 제목", content = "원본 내용"),
                        "친절한 말투",
                        1000,
                    )
                } returns WritingToneDraft(title = "변환 제목", content = "변환 내용")

                val result = service.generateWithWritingTone(user.email, input)

                result shouldBe output
            }
        }
    })
