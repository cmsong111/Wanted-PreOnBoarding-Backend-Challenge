package org.project.portfolio.article.application

import io.github.oshai.kotlinlogging.KotlinLogging
import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.presentation.request.ArticleForm
import org.project.portfolio.article.presentation.request.ArticleUpdateForm
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.common.storage.StorageService
import org.project.portfolio.user.domain.User
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.user.presentation.response.UserSummaryResponse
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ArticleAuthorService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val storageService: StorageService,
) {
    /**
     * 게시글 생성 메소드
     * @param email 유저 이름
     * @articleRequest 게시글 요청 DTO
     */
    @Transactional
    fun createArticle(
        email: String,
        articleForm: ArticleForm,
    ): ArticleResponse {
        // 유저 조회
        val user: User = userRepository.findByEmail(email)
            ?: throw NotFoundException(User::class.java, mapOf("email" to email))

        val article: Article = articleRepository.save(
            Article.create(
                title = articleForm.title,
                content = articleForm.content,
                authorId = user.id,
                images = articleForm.images?.map {
                    storageService.uploadFile(it)
                } ?: emptyList(),
            ),
        )

        return ArticleResponse.from(
            article = article,
            author = UserSummaryResponse.from(user),
        )
    }

    /**
     * 게시글 수정 메소드
     * 스프링 시큐리티를 통해 권한이 있는 사용자만 수정 가능
     * @param articleId 게시글 ID
     * @param articleUpdateForm 게시글 요청 DTO
     */
    @Transactional
    fun updateArticle(
        articleId: Long,
        articleUpdateForm: ArticleUpdateForm,
    ): ArticleResponse {
        // 게시글 조회
        val article: Article = articleRepository.findByIdOrNull(articleId)
            ?: throw NotFoundException(Article::class.java, mapOf("articleId" to articleId))

        // 게시글 수정
        article.update(
            title = articleUpdateForm.title,
            content = articleUpdateForm.content,
            images = articleUpdateForm.images?.map {
                storageService.uploadFile(it)
            },
        )

        return ArticleResponse.from(
            article = article,
            author = UserSummaryResponse.from(
                userRepository.findByIdOrNull(article.authorId),
            ),
        )
    }

    /**
     * 게시글 삭제 메소드
     * 스프링 시큐리티를 통해 권한이 있는 사용자만 삭제 가능
     * @param articleId 게시글 ID
     */
    @Transactional
    fun deleteArticle(articleId: Long) {
        articleRepository.deleteById(articleId)
    }

    /**
     * @param start 시작일: 9일 전
     * @param end 종료일: 9일 전 + 1시간
     */
    @Transactional
    fun sendArticleUpdateNotification(
        start: Instant = Instant.now().minusSeconds(9 * 24 * 60 * 60 + 1 * 60 * 60),
        end: Instant = Instant.now().minusSeconds(9 * 24 * 60 * 60),
    ) {
        // 게시글 조회
        val articles: List<Article> = articleRepository.findByCreatedAtBetween(start, end)
        articles.forEach {
//            notificationService.sendNotification(
//                NotificationRequestDto(
//                    title = "게시글 수정가능 알림",
//                    content = "게시글 '${it.title}'이 내일까지 수정가능합니다",
//                    receiver = it.authorId,
//                    sender = "system",
//                ),
//                "system",
//            )
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
