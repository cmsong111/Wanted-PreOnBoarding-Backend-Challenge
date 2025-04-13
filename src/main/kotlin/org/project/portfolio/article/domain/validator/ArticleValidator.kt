package org.project.portfolio.article.domain.validator

import org.project.portfolio.article.domain.ArticleRepository
import org.project.portfolio.article.domain.exception.ArticleNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit

@Component
class ArticleValidator(
    private val articleRepository: ArticleRepository,
) {
    /**
     * 게시글 작성자인지 확인
     * @param articleId 게시글 ID
     * @param userId 유저 ID
     * @return 게시글 작성자 여부 (true: 작성자, false: 작성자 아님)
     */
    fun isAuthor(
        articleId: Long,
        userId: Long,
    ): Boolean {
        val article = articleRepository.findByIdOrNull(articleId)
            ?: throw ArticleNotFoundException()

        return article.authorId == userId
    }

    /**
     * 게시글이 수정 가능한지 확인
     * <p>작성 후 10일이 지나면 수정 불가능</p>
     * @param articleId 게시글 ID
     * @param userId 유저 ID
     */
    fun isEditable(
        articleId: Long,
        userId: Long,
    ): Boolean {
        val article = articleRepository.findByIdOrNull(articleId)
            ?: throw ArticleNotFoundException()

        if (!isAuthor(articleId, userId)) {
            return false
        }

        val now = Instant.now()

        val diffDays = article.createdAt.until(now, ChronoUnit.DAYS)

        return diffDays < 10
    }
}
