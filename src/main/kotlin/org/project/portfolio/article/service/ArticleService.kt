package org.project.portfolio.article.service

import org.project.portfolio.article.dto.ArticleRequest
import org.project.portfolio.article.dto.ArticleResponseDetail
import org.project.portfolio.article.dto.ArticleResponseHeader
import org.project.portfolio.article.entity.Article
import org.project.portfolio.article.entity.Image
import org.project.portfolio.article.repository.ArticleRepository
import org.project.portfolio.article.repository.ImageRepository
import org.project.portfolio.comment.repository.CommentRepository
import org.project.portfolio.exception_handler.BusinessException
import org.project.portfolio.exception_handler.ErrorCode
import org.project.portfolio.storage.Imagebb
import org.project.portfolio.storage.StorageService
import org.project.portfolio.user.entity.User
import org.project.portfolio.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val imageRepository: ImageRepository,
    private val userRepository: UserRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    @Qualifier("amazonS3")
    private val storageService: StorageService,
    private val imagebb: Imagebb,
) {
    private val logger = LoggerFactory.getLogger(ArticleService::class.java)

    /**
     * 게시글 전체 조회 메소드
     * @param pageable 페이징 정보 객체
     * @param title 검색할 게시글 제목(null일 경우 전체 조회)
     */
    @Transactional(readOnly = true)
    fun getArticles(pageable: Pageable, title: String?): List<ArticleResponseHeader> {
        return articleRepository.findByTitleContains(
            pageable = pageable,
            title = title
        ).map {
            ArticleResponseHeader(
                article = it,
            )
        }
    }

    /**
     * 게시글 단일 조회 메소드
     * @param id 게시글 ID
     */
    @Transactional
    fun getArticle(id: Long, ip: String): ArticleResponseDetail {
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
            logger.info("조회수 증가")
        }

        // 게시글 DTO 반환
        return ArticleResponseDetail(
            article = article,
            commentList = commentRepository.findByArticleId(id, Sort.by(Sort.Direction.ASC, "createdAt")),
            image = imageRepository.findByArticleId(id)
        )
    }

    /**
     * 게시글 생성 메소드
     * @param username 유저 이름
     * @articleRequest 게시글 요청 DTO
     */
    @Transactional
    fun createArticle(username: String, articleRequest: ArticleRequest): ArticleResponseDetail {
        // 유저 조회
        val user: User = userRepository.findById(username).orElseThrow() {
            BusinessException(ErrorCode.USER_NOT_FOUND)
        }

        // 게시글 생성 및 저장
        val article = articleRepository.save(
            Article(
                title = articleRequest.title!!,
                content = articleRequest.content!!,
                author = user
            )
        )

        // 게시글 사진 업로드
        val image: Image? = articleRequest.image?.let {
            val url = storageService.uploadFile(it)
            imageRepository.save(
                Image(
                    url = url,
                    article = article
                )
            )
        }

        // 게시글 저장 및 반환
        return ArticleResponseDetail(
            article = article,
            commentList = emptyList(),
            image = image
        )
    }

    /**
     * 게시글 수정 메소드
     * 스프링 시큐리티를 통해 권한이 있는 사용자만 수정 가능
     * @param id 게시글 ID
     * @param articleRequest 게시글 요청 DTO
     */
    @Transactional
    fun updateArticle(id: Long, articleRequest: ArticleRequest): Article {
        // 게시글 조회
        val article: Article = articleRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }

        // 게시글 수정
        article.update(articleRequest)

        // 게시글 저장 및 반환
        return articleRepository.save(article)
    }

    /**
     * 게시글 삭제 메소드
     * 스프링 시큐리티를 통해 권한이 있는 사용자만 삭제 가능
     * @param id 게시글 ID
     */
    @Transactional
    fun deleteArticle(id: Long) {
        // 게시글 조회
        articleRepository.findById(id).orElseThrow() {
            BusinessException(ErrorCode.ARTICLE_NOT_FOUND)
        }
        // 게시글 삭제
        articleRepository.deleteById(id)
    }

    /**
     * 게시글 Hard 삭제 메소드
     * @param id 게시글 ID
     */
    @Transactional
    fun hardDeleteArticle(id: Long) {
        // 게시글 Hard 삭제
        articleRepository.hardDeleteById(id)
    }

}
