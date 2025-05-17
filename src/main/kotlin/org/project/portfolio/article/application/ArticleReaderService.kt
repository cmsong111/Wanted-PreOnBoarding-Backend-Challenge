package org.project.portfolio.article.application

import org.project.portfolio.article.domain.Article
import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.ArticleView
import org.project.portfolio.article.domain.ArticleViewRepository
import org.project.portfolio.article.domain.CommentRepository
import org.project.portfolio.article.presentation.response.ArticleResponse
import org.project.portfolio.article.presentation.response.ArticleSummaryResponse
import org.project.portfolio.article.presentation.response.CommentResponse
import org.project.portfolio.common.domain.exception.NotFoundException
import org.project.portfolio.user.domain.UserRepository
import org.project.portfolio.user.presentation.response.UserSummaryResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleReaderService(
    private val articleRepository: ArticleRepository,
    private val articleViewRepository: ArticleViewRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
) {
    /**
     * 게시글 전체 조회 메소드
     * @param pageable 페이징 정보 객체
     * @param keyword 검색할 게시글 제목(null일 경우 전체 조회)
     */
    @Transactional(readOnly = true)
    fun getArticles(
        keyword: String?,
        pageable: Pageable,
    ): Page<ArticleSummaryResponse> {
        return articleRepository.findByArticleTitleAndContent(
            pageable = pageable,
        ).map { article ->
            ArticleSummaryResponse.from(
                article = article,
                author = UserSummaryResponse.from(userRepository.findByIdOrNull(article.authorId)),
            )
        }
    }

    /**
     * 게시글 단일 조회 메소드
     * @param id 게시글 ID
     */
    @Transactional(readOnly = true)
    fun getArticle(id: Long): ArticleResponse {
        // 게시글 조회
        val article = articleRepository.findByIdOrNull(id)
            ?: throw IllegalArgumentException("게시글을 찾을 수 없습니다.")
        // 게시글 DTO 반환
        return ArticleResponse.from(
            article = article,
            author = UserSummaryResponse.from(userRepository.findByIdOrNull(article.authorId)),
            comments = commentRepository.findByArticleId(id).map { comment ->
                CommentResponse.from(
                    comment = comment,
                    author = UserSummaryResponse.from(userRepository.findByIdOrNull(comment.authorId)),
                )
            },
        )
    }

    @Transactional
    fun increaseArticleViewCount(
        articleId: Long,
        ip: String,
        userAgent: String,
    ) {
        // 게시글이 존재하지 않는 경우 예외 처리
        if (!articleRepository.existsById(articleId)) {
            throw NotFoundException(Article::class.java, mapOf("articleId" to articleId))
        }

        // 중복 조회수 증가 방지
        if (articleViewRepository.findByIdOrNull(ArticleView.createKey(articleId, ip, userAgent)) == null) {
            articleViewRepository.save(ArticleView.create(articleId, ip, userAgent))
            articleRepository.increaseViewCount(articleId)
        }
    }
}
