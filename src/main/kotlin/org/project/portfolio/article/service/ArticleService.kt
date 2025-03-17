package org.project.portfolio.article.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.project.portfolio.article.controller.request.ArticleForm
import org.project.portfolio.article.controller.response.ArticleDetailResponse
import org.project.portfolio.article.controller.response.ArticleHeaderResponse
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.repository.ArticleRepository
import org.project.portfolio.common.exception.BusinessException
import org.project.portfolio.common.exception.ErrorCode
import org.project.portfolio.common.storage.StorageService
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val storageService: StorageService,
) {


    /**
     * 게시글 전체 조회 메소드
     * @param pageable 페이징 정보 객체
     * @param title 검색할 게시글 제목(null일 경우 전체 조회)
     */
    @Transactional(readOnly = true)
    fun getArticles(
        title: String?,
        pageable: Pageable,
    ): Page<ArticleHeaderResponse> {
        return articleRepository.findByTitleContains(
            title = title,
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
    fun getArticle(
        id: Long,
        ip: String,
    ): ArticleDetailResponse {
        // 게시글 조회
        val article: Article = articleRepository.findById(id).orElseThrow {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }

        // Redis Key
        val redisKey = "article:$id:view:$ip"

        // 중복 조회수 증가 방지: 하루에 한 번만 조회수 증가
        if (!redisTemplate.hasKey(redisKey)) {
            article.viewCount += 1
            articleRepository.save(article)
            redisTemplate.opsForValue()[redisKey, true, 1] = TimeUnit.DAYS
            logger.info { "조회수 증가" }
        }

        // 게시글 DTO 반환
        return ArticleDetailResponse.from(article)
    }

    /**
     * 게시글 생성 메소드
     * @param email 유저 이름
     * @articleRequest 게시글 요청 DTO
     */
    @Transactional
    fun createArticle(
        email: String,
        articleForm: ArticleForm,
    ): ArticleDetailResponse {
        // 유저 조회
        val user: User = userRepository.findById(email).orElseThrow {
            BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        val article: Article = articleRepository.save(
            Article.create(
                title = articleForm.title!!,
                content = articleForm.content!!,
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
     * @param email 게시글 ID
     * @param articleForm 게시글 요청 DTO
     */
    @Transactional
    fun updateArticle(
        email: Long,
        articleForm: ArticleForm,
    ): ArticleDetailResponse {
        // 게시글 조회
        val article: Article = articleRepository.findByIdOrNull(email)
            ?: throw BusinessException(ErrorCode.ARTICLE_NOT_FOUND)


        // 게시글 수정
        article.update(
            title = articleForm.title!!,
            content = articleForm.content!!,
            images = articleForm.images?.map {
                storageService.uploadFile(it)
            },
        )

        return ArticleDetailResponse.from(article)
    }

    /**
     * 게시글 삭제 메소드
     * 스프링 시큐리티를 통해 권한이 있는 사용자만 삭제 가능
     * @param id 게시글 ID
     */
    @Transactional
    fun deleteArticle(id: Long) {
        articleRepository.deleteById(id)
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}
