package org.project.portfolio.writing.tone

import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.Comment
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WritingToneService(
    private val userRepository: UserRepository,
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val writingToneAnalyzer: WritingToneAnalyzer,
    private val writingToneGenerator: WritingToneGenerator,
    private val writingToneRepository: WritingToneRepository,
) {
    @Transactional(readOnly = true)
    fun getSpeeches(
        email: String,
    ): Page<WritingTone> {
        // 유저 조회
        val user = userRepository.findByEmail(email)
            ?: throw NotFoundException(UserRepository::class.java, mapOf("email" to email))

        // 유저의 말투 조회
        return writingToneRepository.findByUserId(
            userId = user.id,
            pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
        )
    }

    /**
     * 유저의 게시글과 댓글을 분석하여 텍스트를 반환하는 메소드
     */
    @Transactional
    fun analyzeSpeech(
        email: String,
    ): WritingTone {
        // 유저 조회
        val user = userRepository.findByEmail(email)
            ?: throw NotFoundException(UserRepository::class.java, mapOf("email" to email))

        // 최근 작성한 게시글 10개
        val article: List<Article> = articleRepository.findByAuthorId(
            authorId = user.id,
            pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
        ).content
        // 최근 작성한 댓글 10개
        val comments: List<Comment> = commentRepository.findByAuthorId(
            authorId = user.id,
            pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")),
        ).content

        // 게시글과 댓글을 합쳐서 분석
        val text: List<String> = article.map { it.title } +
            article.map { it.content } +
            comments.map { it.content }

        // 분석된 텍스트를 SpeechAnalyzer로 분석
        val analyzedText: String = writingToneAnalyzer.analyze(text)

        return writingToneRepository.save(
            WritingTone(
                userId = user.id,
                description = analyzedText,
            ),
        )
    }

    fun createSpeech(
        email: String,
        articleWritingForm: ArticleWritingForm,
    ): ArticleWritingForm {
        // 유저 조회
        val user = userRepository.findByEmail(email)
            ?: throw NotFoundException(UserRepository::class.java, mapOf("email" to email))

        // 가장 최근에 작성한 말투 조회
        val latestSpeech: WritingTone = writingToneRepository.findByUserId(
            userId = user.id,
            pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")),
        ).content.firstOrNull()
            ?: throw NotFoundException(WritingToneRepository::class.java, mapOf("userId" to user.id))

        // 말투 변환
        val convertedArticleWritingForm: ArticleWritingForm = writingToneGenerator.generate(
            articleWritingForm = articleWritingForm,
            toneDescription = latestSpeech.description,
            tokenLimit = 1000,
        )

        return convertedArticleWritingForm
    }
}
