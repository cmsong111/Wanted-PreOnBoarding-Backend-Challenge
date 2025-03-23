package org.project.portfolio.article.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.project.portfolio.article.controller.request.ArticleForm
import org.project.portfolio.article.controller.response.ArticleDetailResponse
import org.project.portfolio.article.controller.response.ArticleHeaderResponse
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.entity.ArticleView
import org.project.portfolio.article.repository.ArticleJpaRepository
import org.project.portfolio.article.repository.ArticleRedisRepository
import org.project.portfolio.common.exception.BusinessException
import org.project.portfolio.common.exception.ErrorCode
import org.project.portfolio.common.storage.StorageService
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleJpaRepository,
    private val articleRedisRepository: ArticleRedisRepository,
    private val userRepository: UserRepository,
    private val storageService: StorageService,
) {
    /**
     * 게시글 전체 조회 메소드
     * @param pageable 페이징 정보 객체
     * @param title 검색할 게시글 제목(null일 경우 전체 조회)
     */
    @Transactional(readOnly = true)
    fun getArticles(
        keyword: String?,
        pageable: Pageable,
    ): Page<ArticleHeaderResponse> {
        return articleRepository.findByArticleTitleAndContent(
            pageable = pageable,
        ).map {
            ArticleHeaderResponse.from(it)
        }
    }

    /**
     * 게시글 단일 조회 메소드
     * @param id 게시글 ID
     */
    @Transactional
    fun getArticle(id: Long): ArticleDetailResponse {
        // 게시글 조회
        val article: Article = articleRepository.findById(id).orElseThrow {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }
        // 게시글 DTO 반환
        return ArticleDetailResponse.from(article)
    }

    @Transactional
    fun increaseArticleViewCount(
        articleId: Long,
        ip: String,
        userAgent: String,
    ) {
        // 게시글 조회
        val article: Article = articleRepository.findByIdOrNull(articleId)
            ?: throw BusinessException(ErrorCode.ARTICLE_NOT_FOUND)

        // 중복 조회수 증가 방지
        articleRedisRepository.findByIdOrNull(
            ArticleView.createKey(articleId, ip, userAgent),
        ) ?: run {
            article.viewCount += 1
            logger.info { "게시글 조회수 증가: ${article.viewCount}" }
            articleRedisRepository.save(
                ArticleView.create(
                    articleId = articleId,
                    ip = ip,
                    userAgent = userAgent,
                ),
            )
        }
    }

    /**
     * 게시글 생성 메소드
     * @param userId 유저 이름
     * @articleRequest 게시글 요청 DTO
     */
    @Transactional
    fun createArticle(
        userId: Long,
        articleForm: ArticleForm,
    ): ArticleDetailResponse {
        // 유저 조회
        val user: User = userRepository.findByIdOrNull(userId)
            ?: throw BusinessException(ErrorCode.USER_NOT_FOUND)

        val article: Article = articleRepository.save(
            Article.create(
                title = articleForm.title,
                content = articleForm.content,
                images = articleForm.images?.map {
                    storageService.uploadFile(it)
                },
                author = user,
            ),
        )

        return ArticleDetailResponse.from(article)
    }

    /**
     * 게시글 수정 메소드
     * 스프링 시큐리티를 통해 권한이 있는 사용자만 수정 가능
     * @param articleId 게시글 ID
     * @param articleForm 게시글 요청 DTO
     */
    @Transactional
    fun updateArticle(
        articleId: Long,
        articleForm: ArticleForm,
    ): ArticleDetailResponse {
        // 게시글 조회
        val article: Article = articleRepository.findByIdOrNull(articleId)
            ?: throw BusinessException(ErrorCode.ARTICLE_NOT_FOUND)

        // 게시글 수정
        article.update(
            title = articleForm.title,
            content = articleForm.content,
            images = articleForm.images?.map {
                storageService.uploadFile(it)
            },
        )

        return ArticleDetailResponse.from(article)
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

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
