package org.project.portfolio.writing.tone.application

import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.Comment
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.common.domain.exception.BadRequestException
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.writing.tone.domain.WritingTone
import org.project.portfolio.writing.tone.domain.WritingToneAnalyzer
import org.project.portfolio.writing.tone.domain.WritingToneDraft
import org.project.portfolio.writing.tone.domain.WritingToneGenerator
import org.project.portfolio.writing.tone.domain.WritingToneRepository
import org.project.portfolio.writing.tone.presentation.request.ArticleWritingForm
import org.project.portfolio.writing.tone.presentation.response.WritingToneResponse
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionTemplate

@Service
class WritingToneCommandService(
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val writingToneAnalyzer: WritingToneAnalyzer,
    private val writingToneGenerator: WritingToneGenerator,
    private val writingToneRepository: WritingToneRepository,
    private val transactionTemplate: TransactionTemplate,
) {
    /**
     * 유저의 최근 게시글·댓글을 바탕으로 말투를 분석하고 저장
     * (외부 AI API 호출을 트랜잭션 밖에서 수행하여 DB 커넥션 점유 최소화)
     */
    fun analyzeWritingTone(email: String): WritingToneResponse {
        val (userId, texts) = checkNotNull(
            transactionTemplate.execute {
                val user = userRepository.findByEmail(email)
                    ?: throw NotFoundException(User::class.java, mapOf("email" to email))

                val recentPage = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
                val articles: List<Article> = articleRepository.findByAuthorId(user.id, recentPage).content
                val comments: List<Comment> = commentRepository.findByAuthorId(user.id, recentPage).content

                val collected: List<String> = articles.map { it.title } +
                    articles.map { it.content } +
                    comments.map { it.content }

                if (collected.isEmpty()) {
                    throw BadRequestException("말투를 분석할 게시글 또는 댓글이 존재하지 않습니다.")
                }
                user.id to collected
            },
        )

        val analyzedText: String = writingToneAnalyzer.analyze(texts)

        return checkNotNull(
            transactionTemplate.execute {
                val saved = writingToneRepository.save(
                    WritingTone(
                        userId = userId,
                        description = analyzedText,
                    ),
                )
                WritingToneResponse.from(saved)
            },
        )
    }

    /**
     * 저장된 최근 말투를 기반으로 글을 변환
     * (외부 AI API 호출을 트랜잭션 밖에서 수행)
     */
    fun generateWithWritingTone(
        email: String,
        articleWritingForm: ArticleWritingForm,
    ): ArticleWritingForm {
        val toneDescription = checkNotNull(
            transactionTemplate.execute {
                val user = userRepository.findByEmail(email)
                    ?: throw NotFoundException(User::class.java, mapOf("email" to email))

                val latestTone: WritingTone = writingToneRepository.findByUserId(
                    userId = user.id,
                    pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")),
                ).content.firstOrNull()
                    ?: throw NotFoundException(WritingTone::class.java, mapOf("userId" to user.id))

                latestTone.description
            },
        )

        val generated = writingToneGenerator.generate(
            draft = WritingToneDraft(
                title = articleWritingForm.title,
                content = articleWritingForm.content,
            ),
            toneDescription = toneDescription,
            tokenLimit = 1000,
        )

        return ArticleWritingForm(
            title = generated.title,
            content = generated.content,
        )
    }
}
