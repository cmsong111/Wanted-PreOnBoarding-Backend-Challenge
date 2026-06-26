package org.project.portfolio.article.application

import io.github.oshai.kotlinlogging.KotlinLogging
import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.event.ArticleEditDeadlineApproachingEvent
import org.project.portfolio.article.presentation.request.ArticleForm
import org.project.portfolio.article.presentation.request.ArticleUpdateForm
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.common.storage.StorageService
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.user.presentation.response.UserSummaryResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionTemplate
import java.time.Clock
import java.time.Instant

@Service
class ArticleCommandService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val storageService: StorageService,
    private val eventPublisher: ApplicationEventPublisher,
    private val transactionTemplate: TransactionTemplate,
    private val clock: Clock,
) {
    /**
     * 게시글 생성 (외부 이미지 업로드는 트랜잭션 밖에서 수행하여 DB 커넥션 점유 최소화)
     */
    fun createArticle(
        email: String,
        articleForm: ArticleForm,
    ): ArticleResponse {
        val uploadedImages = articleForm.images.map { storageService.uploadFile(it) }

        return checkNotNull(
            transactionTemplate.execute {
                val user: User = userRepository.findByEmail(email)
                    ?: throw NotFoundException(User::class.java, mapOf("email" to email))

                val article: Article = articleRepository.save(
                    Article.create(
                        title = articleForm.title,
                        content = articleForm.content,
                        authorId = user.id,
                        images = uploadedImages,
                        tags = articleForm.tags,
                    ),
                )

                ArticleResponse.from(
                    article = article,
                    author = UserSummaryResponse.from(user),
                )
            },
        )
    }

    /**
     * 게시글 수정 (외부 이미지 업로드는 트랜잭션 밖에서 수행)
     */
    fun updateArticle(
        articleId: Long,
        articleUpdateForm: ArticleUpdateForm,
    ): ArticleResponse {
        val uploadedImages = articleUpdateForm.images?.map { storageService.uploadFile(it) }

        return checkNotNull(
            transactionTemplate.execute {
                val article: Article = articleRepository.findByIdOrNull(articleId)
                    ?: throw NotFoundException(Article::class.java, mapOf("articleId" to articleId))

                article.update(
                    title = articleUpdateForm.title,
                    content = articleUpdateForm.content,
                    images = uploadedImages,
                    tags = articleUpdateForm.tags,
                )

                ArticleResponse.from(
                    article = article,
                    author = UserSummaryResponse.from(
                        userRepository.findByIdOrNull(article.authorId),
                    ),
                )
            },
        )
    }

    /**
     * 게시글 삭제 (Soft Delete)
     */
    @Transactional
    fun deleteArticle(articleId: Long) {
        articleRepository.deleteById(articleId)
    }

    /**
     * 작성 9일차 게시글 수정 마감 알림 이벤트 발행
     */
    @Transactional(readOnly = true)
    fun sendArticleUpdateNotification(
        start: Instant = Instant.now(clock).minusSeconds(9 * 24 * 60 * 60 + 1 * 60 * 60),
        end: Instant = Instant.now(clock).minusSeconds(9 * 24 * 60 * 60),
    ) {
        val articles: List<Article> = articleRepository.findByCreatedAtBetween(start, end)
        logger.debug { "9일차 수정 마감 알림 대상 게시글 수: ${articles.size}" }
        articles.forEach { article ->
            eventPublisher.publishEvent(
                ArticleEditDeadlineApproachingEvent(
                    articleId = article.id,
                    articleTitle = article.title,
                    authorId = article.authorId,
                ),
            )
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
