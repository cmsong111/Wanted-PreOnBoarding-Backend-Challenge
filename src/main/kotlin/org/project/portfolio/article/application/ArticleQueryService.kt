package org.project.portfolio.article.application

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.ArticleView
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.article.presentation.response.ArticleSummaryResponse
import org.project.portfolio.article.presentation.response.CommentResponse
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.user.presentation.response.UserSummaryResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
@Transactional(readOnly = true)
class ArticleQueryService(
    private val articleRepository: ArticleRepository,
    private val stringRedisTemplate: StringRedisTemplate,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
) {
    /**
     * 게시글 목록 조회 (키워드 검색 포함, 작성자 배치 조회로 N+1 방지)
     */
    fun getArticles(
        keyword: String?,
        pageable: Pageable,
    ): Page<ArticleSummaryResponse> {
        val articles = articleRepository.searchByKeyword(
            keyword = keyword?.trim()?.takeIf { it.isNotEmpty() },
            pageable = pageable,
        )
        val authorIds = articles.content.map { it.authorId }.distinct()
        val authorsById = userRepository.findAllById(authorIds).associateBy { it.id }

        return articles.map { article ->
            ArticleSummaryResponse.from(
                article = article,
                author = UserSummaryResponse.from(authorsById[article.authorId]),
            )
        }
    }

    /**
     * 게시글 상세 조회 (작성자 및 댓글 작성자 배치 조회로 N+1 방지)
     */
    fun getArticle(id: Long): ArticleResponse {
        val article = articleRepository.findByIdOrNull(id)
            ?: throw NotFoundException(Article::class.java, mapOf("articleId" to id))

        val comments = commentRepository.findByArticleId(id)
        val userIds = (comments.map { it.authorId } + article.authorId).distinct()
        val usersById = userRepository.findAllById(userIds).associateBy { it.id }

        return ArticleResponse.from(
            article = article,
            author = UserSummaryResponse.from(usersById[article.authorId]),
            comments = comments.map { comment ->
                CommentResponse.from(
                    comment = comment,
                    author = UserSummaryResponse.from(usersById[comment.authorId]),
                )
            },
        )
    }

    /**
     * 조회수 증가 (Redis SETNX 원자 연산으로 TOCTOU 동시성 방지 + Redis 장애 격리)
     */
    @Transactional
    fun increaseArticleViewCount(
        articleId: Long,
        ip: String,
        userAgent: String,
    ) {
        if (!articleRepository.existsById(articleId)) {
            throw NotFoundException(Article::class.java, mapOf("articleId" to articleId))
        }

        try {
            val viewKey = "$ARTICLE_VIEW_KEY_PREFIX${ArticleView.createKey(articleId, ip, userAgent)}"
            val isFirstView = stringRedisTemplate.opsForValue()
                .setIfAbsent(viewKey, "1", VIEW_TTL) == true

            if (isFirstView) {
                articleRepository.increaseViewCount(articleId)
                logger.info {
                    mapOf("articleId" to articleId, "ip" to ip, "userAgent" to userAgent)
                }
            }
        } catch (e: Exception) {
            logger.warn(e) { "조회수 증가 처리 실패 (Redis 장애 격리): articleId=$articleId" }
        }
    }

    companion object {
        private const val ARTICLE_VIEW_KEY_PREFIX = "article_views:"
        private val VIEW_TTL: Duration = Duration.ofHours(24)
        private val logger: KLogger = KotlinLogging.logger {}
    }
}
